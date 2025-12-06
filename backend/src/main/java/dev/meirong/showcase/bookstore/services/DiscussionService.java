package dev.meirong.showcase.bookstore.services;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import dev.meirong.showcase.bookstore.dto.DiscussionDTO;
import dev.meirong.showcase.bookstore.entities.Discussion;
import dev.meirong.showcase.bookstore.entities.User;
import dev.meirong.showcase.bookstore.mapper.EntityMapper;
import dev.meirong.showcase.bookstore.repositories.DiscussionRepository;
import dev.meirong.showcase.bookstore.repositories.UserRepository;
import dev.meirong.showcase.bookstore.utils.ErrorsUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscussionService {

    private final EntityMapper entityMapper;
    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;


    public Page<DiscussionDTO> findAllByUserEmail(String userEmail, Pageable pageable) {

        User user = getUserFromRepository(userEmail);

        Page<Discussion> discussions = discussionRepository.findByDiscussionHolder(user, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        List<DiscussionDTO> pageContent = new ArrayList<>();

        for (Discussion discussion : discussions) {
            DiscussionDTO discussionDTO = convertToDiscussionDTO(discussion);
            discussionDTO.setUserEmail(userEmail);
            discussionDTO.setUserFirstName(user.getFirstName());
            discussionDTO.setUserLastName(user.getLastName());
            pageContent.add(discussionDTO);
        }

        return new PageImpl<>(pageContent, discussions.getPageable(), discussions.getTotalElements());
    }

    public Page<DiscussionDTO> findAllByClosed(Pageable pageable) {

        Page<Discussion> discussions = discussionRepository.findByClosed(false, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        List<DiscussionDTO> pageContent = new ArrayList<>();

        for (Discussion discussion : discussions) {
            User user = discussion.getDiscussionHolder();
            DiscussionDTO discussionDTO = convertToDiscussionDTO(discussion);
            discussionDTO.setUserEmail(user.getEmail());
            discussionDTO.setUserFirstName(user.getFirstName());
            discussionDTO.setUserLastName(user.getLastName());
            pageContent.add(discussionDTO);
        }

        return new PageImpl<>(pageContent, discussions.getPageable(), discussions.getTotalElements());
    }

    @Transactional
    public DiscussionDTO addDiscussion(String userEmail, DiscussionDTO discussionDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnDiscussionError("Some fields are invalid.", bindingResult, HttpStatus.FORBIDDEN);
        }

        User user = getUserFromRepository(userEmail);

        Discussion discussion = convertToDiscussion(discussionDTO);
        discussion.setDiscussionHolder(user);
        discussion.setClosed(false);
        user.getDiscussions().add(discussion);

        Discussion savedDiscussion = discussionRepository.save(discussion);

        return convertToDiscussionDTO(savedDiscussion);
    }

    @Transactional
    public void updateDiscussion(String adminEmail, DiscussionDTO discussionDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            ErrorsUtil.returnDiscussionError("Some fields are invalid.", bindingResult, HttpStatus.FORBIDDEN);
        }

        if (discussionDTO.getResponse() == null || discussionDTO.getResponse().isEmpty() || discussionDTO.getResponse().replaceAll(" *", "").isEmpty()) {
            ErrorsUtil.returnDiscussionError("Discussion cannot be closed without administration response", null, HttpStatus.FORBIDDEN);
        }

        Optional<Discussion> discussionOptional = discussionRepository.findById(discussionDTO.getId());

        if (discussionOptional.isEmpty()) {
            ErrorsUtil.returnDiscussionError("Discussion not found.", null, HttpStatus.NOT_FOUND);
            return; // Prevent accessing get() if not present
        }

        Discussion discussion = discussionOptional.get();

        if (discussion.getClosed()) {
            ErrorsUtil.returnDiscussionError("This discussion is already closed.", null, HttpStatus.FORBIDDEN);
        }

        discussion.setAdminEmail(adminEmail);
        discussion.setResponse(discussionDTO.getResponse());
        discussion.setClosed(true);

        discussionRepository.save(discussion);
    }


    private User getUserFromRepository(String userEmail) {

        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isEmpty()) {
            ErrorsUtil.returnUserError("User with such email is not found.", null, HttpStatus.NOT_FOUND);
            throw new IllegalArgumentException("User with such email is not found.");
        }

        return user.get();
    }

    private DiscussionDTO convertToDiscussionDTO(Discussion discussion) {
        return entityMapper.toDiscussionDTO(discussion);
    }

    private Discussion convertToDiscussion(DiscussionDTO discussionDTO) {
        return entityMapper.toDiscussion(discussionDTO);
    }
}
