package com.igreja.GestaoQuadrangular.servicce; // ← corrigido

import com.igreja.GestaoQuadrangular.application.dto.ApresentacaoCriancaRequestDTO;
import com.igreja.GestaoQuadrangular.application.dto.ApresentacaoCriancaResponseDTO;
import com.igreja.GestaoQuadrangular.domain.entity.ApresentacaoCrianca;
import com.igreja.GestaoQuadrangular.domain.entity.Membro;
import com.igreja.GestaoQuadrangular.domain.entity.Pastor;
import com.igreja.GestaoQuadrangular.domain.entity.Usuario;
import com.igreja.GestaoQuadrangular.domain.repository.ApresentacaoCriancaRepository;
import com.igreja.GestaoQuadrangular.domain.repository.MembroRepository;
import com.igreja.GestaoQuadrangular.domain.repository.PastorRepository;
import com.igreja.GestaoQuadrangular.domain.repository.UsuarioRepository;
import com.igreja.GestaoQuadrangular.num.Role;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApresentacaoCriancaService {

    private final PastorRepository pastorRepository;
    private final ApresentacaoCriancaRepository apresentacaoCriancaRepository;
    private final MembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository; // ← para buscar o pastor

    public ApresentacaoCriancaService(
            PastorRepository pastorRepository, ApresentacaoCriancaRepository apresentacaoCriancaRepository,
            MembroRepository membroRepository,
            UsuarioRepository usuarioRepository) {
        this.pastorRepository = pastorRepository;
        this.apresentacaoCriancaRepository = apresentacaoCriancaRepository;
        this.membroRepository = membroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // =========================
    // GERAR CERTIFICADO PDF
    // =========================
    public byte[] gerarCertificadoPdf(Long apresentacaoId) throws Exception {
        ApresentacaoCrianca ap = apresentacaoCriancaRepository.findById(apresentacaoId)
                .orElseThrow(() -> new RuntimeException("Apresentação não encontrada"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(100, 50, 80, 50);

        document.add(new Paragraph("CERTIFICADO DE APRESENTAÇÃO DE CRIANÇA")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Igreja do Evangelho Quadrangular")
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n\n"));

        document.add(new Paragraph("Certificamos que a criança:")
                .setFontSize(14));

        document.add(new Paragraph(ap.getNomeCrianca())
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("nascida em " +
                (ap.getDataNascimentoCrianca() != null ? ap.getDataNascimentoCrianca().toString() : "não informada"))
                .setFontSize(14));

        document.add(new Paragraph("\nfoi apresentada ao Senhor em " + ap.getDataApresentacao())
                .setFontSize(14));

        document.add(new Paragraph("pelos pais:")
                .setFontSize(14));

        String pais = "";
        if (ap.getPai() != null) pais += ap.getPai().getNome();
        if (ap.getMae() != null) pais += (pais.isEmpty() ? "" : " e ") + ap.getMae().getNome();

        document.add(new Paragraph(pais.isEmpty() ? "não informados" : pais)
                .setFontSize(14));

        // Pastor vem do Usuario
        String nomePastor = (ap.getPastorOficiante() != null && ap.getPastorOficiante().getUsuario() != null)
                ? ap.getPastorOficiante().getUsuario().getNomeCompleto()
                : "não informado";

        document.add(new Paragraph("\noficiada pelo Pastor " + nomePastor)
                .setFontSize(14));

        document.add(new Paragraph("\n\n\"E dizia: Deixai vir os meninos a mim...\" Mc 10:14")
                .setFontSize(12)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("\n\n\n________________________________________")
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Assinatura do Pastor")
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }

    // =========================
    // CADASTRAR APRESENTAÇÃO
    // =========================
    @Transactional
    public ApresentacaoCriancaResponseDTO cadastrar(ApresentacaoCriancaRequestDTO request) {
        ApresentacaoCrianca apresentacao = new ApresentacaoCrianca();

        // Campos básicos
        apresentacao.setNomeCrianca(request.nomeCrianca());
        apresentacao.setDataNascimentoCrianca(request.dataNascimentoCrianca());
        apresentacao.setDataApresentacao(request.dataApresentacao());
        apresentacao.setObservacoes(request.observacoes());
        apresentacao.setTelefoneContato(request.telefoneContato());

        // =========================
        // TRATAMENTO DOS PAIS (CORRIGIDO E COMPLETO)
        // =========================
        boolean temPai = false;
        boolean temMae = false;

        // Pai
        if (request.paiMembroId() != null) {
            Membro pai = membroRepository.findById(request.paiMembroId())
                    .orElseThrow(() -> new EntityNotFoundException("Pai membro não encontrado com ID: " + request.paiMembroId()));
            apresentacao.setPai(pai);
            temPai = true;
        } else if (request.nomePaiVisitante() != null && !request.nomePaiVisitante().trim().isEmpty()) {
            apresentacao.setNomePaiVisitante(request.nomePaiVisitante().trim());
            temPai = true;
        }

        // Mãe (você esqueceu de implementar essa parte!)
        if (request.maeMembroId() != null) {
            Membro mae = membroRepository.findById(request.maeMembroId())
                    .orElseThrow(() -> new EntityNotFoundException("Mãe membro não encontrada com ID: " + request.maeMembroId()));
            apresentacao.setMae(mae);
            temMae = true;
        } else if (request.nomeMaeVisitante() != null && !request.nomeMaeVisitante().trim().isEmpty()) {
            apresentacao.setNomeMaeVisitante(request.nomeMaeVisitante().trim());
            temMae = true;
        }

        // Validação: pelo menos um dos pais deve ser informado
        if (!temPai && !temMae) {
            throw new IllegalArgumentException("É obrigatório informar pelo menos um dos pais (como membro ou visitante).");
        }

        // =========================
        // PASTOR OFICIANTE (JÁ ESTÁ CORRETO!)
        // =========================
        Pastor pastor = pastorRepository.findByUsuario(getUsuarioLogado())
                .orElseThrow(() -> new IllegalArgumentException("Não existe registro de Pastor associado a este usuário"));

        apresentacao.setPastorOficiante(pastor);

        // =========================
        // GARANTIA FINAL: data_cadastro nunca null
        // =========================
        // Mesmo com @CreationTimestamp, forçamos aqui para total segurança
        apresentacao.setDataCadastro(LocalDate.now());

        // Salva
        ApresentacaoCrianca salva = apresentacaoCriancaRepository.save(apresentacao);

        return toResponseDTO(salva);
    }

    // Método auxiliar para pegar o usuário logado (já usado em outros serviços)
    private Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Usuário não autenticado");
        }
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    // =========================
    // LISTAR PRÓXIMAS
    // =========================
    public List<ApresentacaoCriancaResponseDTO> listarProximas() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(60);

        return apresentacaoCriancaRepository.findByDataApresentacaoBetween(hoje, limite)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // DTO CONVERTER
    // =========================
    private ApresentacaoCriancaResponseDTO toResponseDTO(ApresentacaoCrianca ap) {
        return new ApresentacaoCriancaResponseDTO(
                ap.getId(),
                ap.getNomeCrianca(),
                ap.getDataNascimentoCrianca(),
                ap.getDataApresentacao(),

                // Pais
                ap.getNomePaiExibicao(),
                ap.getNomeMaeExibicao(),

                // Pastor (corrigido!)
                ap.getNomePastorExibicao(),     // ← melhor opção

                // ou se preferir inline:
                // (ap.getPastorOficiante() != null) ? ap.getPastorOficiante().getNomeCompleto() : null,

                // Campos extras
                ap.isPaisMembros(),
                ap.getTelefoneContato(),
                ap.getObservacoes()
        );
    }
}