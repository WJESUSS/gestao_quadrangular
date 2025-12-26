package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.*;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Contribuicao;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.repository.*;
import com.igreja.GestaoQuadrangular.num.TipoOferta;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TesourariaService {

    private final ContribuicaoRepository contribuicaoRepository;
    private final MembroRepository membroRepository;
    private final CelulaRepository celulaRepository;

    public TesourariaService(ContribuicaoRepository contribuicaoRepository,
                             MembroRepository membroRepository,
                             CelulaRepository celulaRepository) {
        this.contribuicaoRepository = contribuicaoRepository;
        this.membroRepository = membroRepository;
        this.celulaRepository = celulaRepository;
    }

    @Transactional
    public Contribuicao registrar(ContribuicaoCreateDTO dto) {
        Membro membro = membroRepository.findById(dto.membroId())
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        Contribuicao c = new Contribuicao();
        c.setMembro(membro);
        c.setTipoOferta(dto.tipoOferta());
        c.setValor(dto.valor());
        c.setData(dto.data());
        c.setObservacao(dto.observacao());
        c.setNumeroRelatorioSecretaria(dto.numeroRelatorioSecretaria());

        return contribuicaoRepository.save(c);
    }

    @Transactional(readOnly = true)
    public RelatorioFinanceiroDTO gerarRelatorio(LocalDate inicio, LocalDate fim) {
        // Total de dízimos — usa COALESCE no repository ou verifica null aqui
        BigDecimal totalDizimos = contribuicaoRepository.sumByTipoOfertaAndDataBetween(TipoOferta.DIZIMO, inicio, fim);
        if (totalDizimos == null) {
            totalDizimos = BigDecimal.ZERO;
        }

        // Total de ofertas (excluindo dízimos)
        BigDecimal totalOfertas = contribuicaoRepository.findByDataBetween(inicio, fim).stream()
                .filter(c -> c.getTipoOferta() != TipoOferta.DIZIMO)
                .map(Contribuicao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Agora é seguro somar
        BigDecimal totalGeral = totalDizimos.add(totalOfertas);

        // Entradas por célula
        List<EntradaPorCelulaDTO> porCelula = celulaRepository.findAll().stream()
                .map(celula -> {
                    BigDecimal valor = contribuicaoRepository.sumByCelulaIdAndDataBetween(celula.getId(), inicio, fim);
                    BigDecimal valorSeguro = valor != null ? valor : BigDecimal.ZERO;
                    return new EntradaPorCelulaDTO(celula.getId(), celula.getNome(), valorSeguro);
                })
                .filter(e -> e.total().compareTo(BigDecimal.ZERO) > 0)
                .sorted((a, b) -> b.total().compareTo(a.total()))
                .toList();

        return new RelatorioFinanceiroDTO(inicio, fim, totalDizimos, totalOfertas, totalGeral, porCelula);
    }

    @Transactional(readOnly = true)
    public byte[] gerarPdfRelatorio(LocalDate inicio, LocalDate fim) {
        RelatorioFinanceiroDTO rel = gerarRelatorio(inicio, fim);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("RELATÓRIO FINANCEIRO")
                    .setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Período: " + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                    " a " + fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Resumo Geral")
                    .setBold().setFontSize(14));
            document.add(new Paragraph("Total de Dízimos: R$ " + rel.totalDizimos()));
            document.add(new Paragraph("Total de Ofertas: R$ " + rel.totalOfertas()));
            document.add(new Paragraph("Total Geral: R$ " + rel.totalGeral()).setBold());
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Entradas por Célula")
                    .setBold().setFontSize(14));

            Table table = new Table(3);
            table.addHeaderCell("Célula");
            table.addHeaderCell("Líder");
            table.addHeaderCell("Total (R$)");

            rel.entradasPorCelula().forEach(e -> {
                Celula celula = celulaRepository.findById(e.celulaId()).orElse(null);
                String lider = celula != null && celula.getLider() != null ? celula.getLider().getNome() : "Sem líder";
                table.addCell(e.celulaNome());
                table.addCell(lider);
                table.addCell(e.total().toString());
            });

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF financeiro", e);
        }
    }
}