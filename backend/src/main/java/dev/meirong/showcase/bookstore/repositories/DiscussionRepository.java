package dev.meirong.showcase.bookstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.meirong.showcase.bookstore.entities.Discussion;
import dev.meirong.showcase.bookstore.entities.User;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    Page<Discussion> findByDiscussionHolder(User user, Pageable pageable);

    Page<Discussion> findByClosed(boolean isClosed, Pageable pageable);
}
