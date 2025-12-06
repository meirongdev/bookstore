package dev.meirong.showcase.bookstore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutDTO {

    private BookDTO bookDTO;

    private Integer daysLeft;
}
