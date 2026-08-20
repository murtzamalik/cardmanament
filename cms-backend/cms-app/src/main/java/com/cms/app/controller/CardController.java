package com.cms.app.controller;

import com.cms.app.config.AESencryption;
import com.cms.app.request.CardAvailableLimitRequest;
import com.cms.app.request.CardInquiryRequest;
import com.cms.app.request.CardLimitValidateRequest;
import com.cms.app.request.CardNewRequest;
import com.cms.app.request.CardSpendingSummaryRequest;
import com.cms.app.request.CardUpdateStatusRequest;
import com.cms.app.request.CardValidationRequest;
import com.cms.app.request.ChangePinRequest;
import com.cms.app.request.ForgotPin;
import com.cms.app.response.CardInquiryResponse;
import com.cms.app.response.CardLimitValidateResponse;
import com.cms.app.response.CardLovResponse;
import com.cms.app.response.CardSpendingSummaryResponse;
import com.cms.app.response.ResponseWrapper;
import com.cms.app.service.CardService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;
    private final AESencryption aesencryption;

    public CardController(CardService cardService, AESencryption aesencryption) {
        this.cardService = cardService;
        this.aesencryption = aesencryption;
    }

    @PostMapping("/inquiry")
    public @ResponseBody ResponseWrapper<CardInquiryResponse> inquiry(@RequestBody CardInquiryRequest request) {
        return cardService.inquiry(request);
    }

    @PostMapping("/new-request")
    public @ResponseBody ResponseWrapper<Void> newRequest(@Valid @RequestBody CardNewRequest request) {
        return cardService.newRequest(request);
    }

    @PostMapping("/update-status")
    public @ResponseBody ResponseWrapper<Void> updateStatus(@Valid @RequestBody CardUpdateStatusRequest request) {
        return cardService.updateStatus(request);
    }

    @GetMapping("/lov/{slug}")
    public @ResponseBody ResponseWrapper<List<CardLovResponse>> getLov(@PathVariable String slug) {
        return cardService.getLov(slug);
    }

    @PostMapping("/generate-pin")
    public @ResponseBody ResponseWrapper<Void> generatePin(@RequestBody ForgotPin request) {
        return cardService.forgotPin(request);
    }

    @PostMapping("/change-pin")
    public @ResponseBody ResponseWrapper<Void> changePin(@Valid @RequestBody ChangePinRequest request) {
        return cardService.changePin(request);
    }

    @PostMapping("/validate")
    public @ResponseBody ResponseWrapper<Void> validate(@RequestBody CardValidationRequest request) {
        if (request.getPin() != null && !request.getPin().isEmpty()) {
            System.out.println("Encrypted pin " + aesencryption.encryptwith256(request.getPin()));
        }
        return cardService.validate(request);
    }

    @PostMapping("/limit/validate")
    public @ResponseBody ResponseWrapper<CardLimitValidateResponse> validateLimit(@RequestBody CardLimitValidateRequest request) {
        return cardService.validateLimit(request);
    }

    @PostMapping("/limit/available")
    public @ResponseBody ResponseWrapper<CardLimitValidateResponse> availableLimit(@RequestBody CardAvailableLimitRequest request) {
        return cardService.availableLimit(request);
    }

    @PostMapping("/spending-summary")
    public @ResponseBody ResponseWrapper<List<CardSpendingSummaryResponse>> spendingSummary(
            @RequestBody CardSpendingSummaryRequest request) {
        return cardService.spendingSummary(request);
    }
}
