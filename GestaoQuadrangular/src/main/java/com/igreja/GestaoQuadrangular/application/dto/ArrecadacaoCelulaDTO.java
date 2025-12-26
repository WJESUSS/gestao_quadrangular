package com.igreja.GestaoQuadrangular.application.dto;

import java.math.BigDecimal;

public record ArrecadacaoCelulaDTO(Long celulaId, String celulaNome, String liderNome, BigDecimal total) {}
