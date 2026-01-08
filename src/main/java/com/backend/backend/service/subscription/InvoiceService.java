package com.backend.backend.service.subscription;

import com.backend.backend.dto.response.Subscription.InvoiceResponse;
import com.backend.backend.mapper.Subscription.InvoiceMapper;
import com.backend.backend.repository.subscription.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(
            InvoiceMapper invoiceMapper,
            InvoiceRepository invoiceRepository
    ) {
        this.invoiceMapper = invoiceMapper;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(invoiceMapper::toInvoiceResponse).toList();
    }
}
