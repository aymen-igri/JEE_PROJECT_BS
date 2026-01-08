package com.backend.backend.mapper.Subscription;

import com.backend.backend.dto.response.Subscription.InvoiceResponse;
import com.backend.backend.entity.subscription.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    private final PaymentMapper paymentMapper;

    public InvoiceMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    public InvoiceResponse toInvoiceResponse(Invoice invoice){
        return new InvoiceResponse(
                invoice.getInvoiceId(),
                paymentMapper.toPaymentDTO(invoice.getPayment()),
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getTotalAmount(),
                invoice.getTaxAmount(),
                invoice.getStatus(),
                invoice.getPdfPath()
        );
    }

}
