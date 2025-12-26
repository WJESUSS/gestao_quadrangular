package com.igreja.GestaoQuadrangular.application.dto;

import java.math.BigDecimal;

public record EntradaPorCelulaDTO(Long celulaId, String celulaNome, BigDecimal total) {}