package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.application.dto.CelulaReportDTO;
import com.igreja.GestaoQuadrangular.domain.entity.Celula;
import com.igreja.GestaoQuadrangular.domain.entity.Lider;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.repository.CelulaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LeaderService {
    @Autowired
    private JavaMailSender emailSender;

    private final MembroRepository membroRepository;
    private final CelulaRepository celulaRepository;

    public LeaderService(MembroRepository membroRepository, CelulaRepository celulaRepository) {
        this.membroRepository = membroRepository;
        this.celulaRepository = celulaRepository;
    }

    @Transactional(readOnly = true)
    public CelulaReportDTO gerarRelatorio(Long celulaId) {
        Celula celula = celulaRepository.buscarComLider(celulaId);

        if (celula == null) {
            throw new RuntimeException("Célula não encontrada");
        }

        List<Membro> membros = membroRepository.findByCelulaId(celulaId);

        return new CelulaReportDTO(celula, membros);
    }


    public byte[] gerarPdf(Long celulaId) {
        CelulaReportDTO relatorio = gerarRelatorio(celulaId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Cabeçalho
            document.add(new Paragraph("Relatório da Célula: " + relatorio.getCelulaNome()));
            document.add(new Paragraph("Líder: " + relatorio.getLiderNome()));
            document.add(new Paragraph("Total de membros: " + relatorio.getTotalMembros()));
            document.add(new Paragraph("Data do relatório: "
                    + relatorio.getDataRelatorio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setMarginBottom(20));

            // Tabela
            Table table = new Table(4);
            table.addCell("Nome");
            table.addCell("Email");
            table.addCell("Faltas");
            table.addCell("Status");

            // Preenchendo tabela
            relatorio.getMembros().forEach(m -> {
                table.addCell(m.getNome());
                table.addCell(m.getEmail());
                table.addCell(String.valueOf(m.getFaltasConsecutivas()));
                table.addCell(m.getStatus().name()); // caso seja enum, converte para String
            });

            document.add(table);
            document.close(); // fecha o documento

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
    public void enviarRelatorioPorEmail(Long celulaId, String emailPastor) {
        byte[] pdfBytes = gerarPdf(celulaId);
        CelulaReportDTO relatorio = gerarRelatorio(celulaId);

        // Usando MimeMessagePreparator para melhor tratamento assíncrono (boa prática)
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(emailPastor);
                helper.setSubject("Relatório da Célula - " + relatorio.getCelulaNome());
                helper.setText("Prezado Pastor,\n\nEm anexo o relatório atualizado da célula.\n\nAtenciosamente,\nSistema de Gestão Quadrangular");
                // Anexa o PDF
                helper.addAttachment("Relatorio_Celula_" + relatorio.getCelulaNome() + ".pdf",
                        new ByteArrayResource(pdfBytes));
            }
        };

        try {
            emailSender.send(preparator);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail para o pastor: " + emailPastor, e);
        }
    }
    @Transactional(readOnly = true)
    public boolean isLeaderOfCell(String username, Long celulaId) {
        return celulaRepository.findById(celulaId)
                .map(celula -> celula.getLider().getEmail().equals(username)
                        || celula.getLider().getUsername().equals(username))
                .orElse(false);
    }

}

