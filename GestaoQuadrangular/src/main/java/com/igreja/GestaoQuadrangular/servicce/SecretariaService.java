package com.igreja.GestaoQuadrangular.servicce;

import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.TransferenciaMembresia;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class SecretariaService {

    private final MembroRepository membroRepository;

    public SecretariaService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    public byte[] gerarPdfCartaTransferencia(TransferenciaMembresia transferencia) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("CARTA DE TRANSFERÊNCIA DE MEMBRESIA")
                .setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Membro: " + transferencia.getMembro().getNome()));
        document.add(new Paragraph("Tipo: " + transferencia.getTipo()));
        document.add(new Paragraph("Igreja: " + transferencia.getIgrejaOrigemDestino()));
        document.add(new Paragraph("Data: " + transferencia.getDataTransferencia()));

        document.close();
        return baos.toByteArray();
    }

    public List<Membro> listarAniversariantesMes(int mes) {
        // Aqui você precisa implementar a query no MembroRepository:
        // Exemplo: findByDataNascimentoMonth(mes)
        return membroRepository.findByDataNascimentoMonth(mes);
    }
}
