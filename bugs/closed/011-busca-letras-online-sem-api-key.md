# BUG-011 — Busca e Importação de Letras Online 100% Sem Chaves de API

- **Status:** Closed

- **Severidade:** Alto | **Tipo:** Integração Externa / Quebra de API Legada | **Fase:** 3
- **Problema:** Falta uma interface funcional para buscar e importar letras da internet diretamente pelo aplicativo (semelhante à busca do Vagalume no Holyrics original), utilizando uma fonte gratuita que funcione hoje **100% sem necessidade de chaves de API**.
- **Arquivos envolvidos:**
  - [vagalume-client.ts](../../new-holyrics/slices/songs/daemon/vagalume-client.ts#L42-L153)
  - [vagalume-router.ts](../../new-holyrics/slices/songs/daemon/routes/vagalume-router.ts#L17-L62)
  - Referências Java originais: [VagalumeAPI.java](../../holyrics-source/legacy-java/com/limagiran/vagalumeapi/VagalumeAPI.java#L14-L52) e [MusicWebSearch.java](../../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicWebSearch.java#L288-L350)
- **Causa-raiz (Verificada em teste real):** A API usada pelo Holyrics original e por `vagalume-client.ts` (`https://api.vagalume.com.br/search.excerpt`) está retornando **HTTP 503 Service Temporarily Unavailable**, além de `search.php` exigir `apikey`/captcha. Por outro lado, testamos **LRCLIB (`https://lrclib.net/api/search`)** e **Letras.mus.br (`https://solr.sscdn.co/letras/m1/`)**, ambos respondendo **HTTP 200** sem nenhuma chave de API.
- **Solução planejada:**
  1. Atualizar o serviço de busca online em `vagalume-client.ts` / `vagalume-router.ts` para consultar **Letras.mus.br** (com parsing da página pública da letra) e **LRCLIB** (`plainLyrics`), 100% sem API Key.
  2. Adicionar aba **"Buscar na Internet"** dentro do modal de músicas (e botão na barra de Letras), permitindo pesquisar, pré-visualizar os slides e importar direto para o editor/acervo.
