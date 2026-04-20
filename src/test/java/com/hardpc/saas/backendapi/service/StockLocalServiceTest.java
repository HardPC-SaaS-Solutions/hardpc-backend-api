package com.hardpc.saas.backendapi.service;

import com.hardpc.saas.backendapi.entity.StockLocal;
import com.hardpc.saas.backendapi.repository.StockLocalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class StockLocalServiceTest {

    private final StockLocalRepository repository = Mockito.mock(StockLocalRepository.class);
    private final StockLocalService service = new StockLocalService(repository);

    @Test
    void guardar() {
        StockLocal stock = new StockLocal();

        Mockito.when(repository.save(stock)).thenReturn(stock);

        assertNotNull(service.guardar(stock));
    }
}