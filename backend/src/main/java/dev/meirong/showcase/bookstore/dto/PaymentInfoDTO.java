package dev.meirong.showcase.bookstore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInfoDTO {

    private int amount;

    private String currency;

    private String receiptEmail;
}
