package com.backend.backend.service.subscription;

import com.backend.backend.dto.response.Subscription.PaymentResponse;
import com.backend.backend.mapper.Subscription.InvoiceMapper;
import com.backend.backend.mapper.Subscription.PaymentMapper;
import com.backend.backend.repository.subscription.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;


    public PaymentService(
            PaymentMapper paymentMapper,
            PaymentRepository paymentRepository
    ) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(paymentMapper::toPaymentDTO).toList();
    }
}
