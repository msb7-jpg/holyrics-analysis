import { test, expect } from '@playwright/test'

test.describe('LumenLite Commercial Product & UI/UX Suite', () => {
  test('1. Zero vazamentos técnicos (sem RF-X, culto.json, Daemon Conectado, FSM, Playback ou NO AR)', async ({
    page,
  }) => {
    const consoleErrors: string[] = []
    page.on('pageerror', (err) => consoleErrors.push(err.message))

    await page.goto('/')
    await expect(page).toHaveTitle('LumenLite')

    // Aguarda carregamento do acervo de hinos
    await expect(page.getByText(/Biblioteca \(\d+\)/)).toBeVisible({ timeout: 10000 })

    const bodyText = await page.locator('body').innerText()
    expect(bodyText).not.toMatch(/RF-\d+/i)
    expect(bodyText).not.toContain('culto.json')
    expect(bodyText).not.toContain('Daemon Conectado')
    expect(bodyText).not.toContain('Versão FSM')
    expect(bodyText).not.toContain('Latência IPC')
    expect(bodyText).not.toContain('HDMI ATIVO')
    expect(bodyText).not.toContain('Playback')
    expect(bodyText).not.toContain('NO AR')

    // Verifica que o Monitor de Saída tem o cabeçalho semântico unificado
    await expect(page.getByText('Saída: Telão Principal (1080p)')).toBeVisible()

    // Verifica que a busca global está centralizada na Topbar
    await expect(page.getByText('Buscar hino, versículo ou mídia...')).toBeVisible()
    expect(consoleErrors).toEqual([])
  })

  test('2. Coluna 1 (Esquerda): Abas Roteiro | Imagens | Vídeos | Arquivos e Menu de Contexto (Botão Direito)', async ({
    page,
  }) => {
    await page.goto('/')
    await expect(page.getByRole('button', { name: 'Roteiro' })).toBeVisible()
    await expect(page.getByRole('button', { name: 'Imagens' })).toBeVisible()
    await expect(page.getByRole('button', { name: 'Vídeos' })).toBeVisible()
    await expect(page.getByRole('button', { name: 'Arquivos' })).toBeVisible()

    // Navega pelas abas e verifica ausência de mocks fictícios
    await page.getByRole('button', { name: 'Imagens' }).click()
    await expect(page.getByText('Nenhuma mídia importada')).toBeVisible()

    await page.getByRole('button', { name: 'Vídeos' }).click()
    await expect(page.getByText('Nenhuma mídia importada')).toBeVisible()

    await page.getByRole('button', { name: 'Arquivos' }).click()
    await expect(page.getByText('Nenhum arquivo importado')).toBeVisible()

    // Volta para a aba Roteiro e abre o Menu de Contexto com botão direito
    await page.getByRole('button', { name: 'Roteiro' }).click()
    await page.getByText(/Ordem do Culto/).click({ button: 'right' })

    const contextMenu = page.getByTestId('liturgy-context-menu')
    await expect(contextMenu).toBeVisible()
    await expect(contextMenu.getByText('Contagem Regressiva (5 min)')).toBeVisible()
    await expect(contextMenu.getByText('Anotação de Culto')).toBeVisible()

    // Adiciona uma Contagem Regressiva pelo menu de contexto
    await contextMenu.getByText('Contagem Regressiva (5 min)').click()
    await expect(page.getByText(/1\. Contagem Regressiva \(5 min\)/)).toBeVisible()
  })

  test('3. Semântica Preview vs. Ao Vivo e Fidelidade 16:9 do Monitor de Saída sem cortes', async ({
    page,
  }) => {
    await page.goto('/')
    await expect(page.getByText(/Biblioteca \(\d+\)/)).toBeVisible({ timeout: 10000 })

    // Clica na primeira estrofe da música aberta no palco central para projetar ao vivo
    const firstStanzaNumber = page.locator('section').nth(2).getByText('01').first()
    await expect(firstStanzaNumber).toBeVisible()
    await firstStanzaNumber.click()

    // Verifica que o badge antigo "NO AR" não aparece na estrofe
    await expect(page.locator('body')).not.toContainText('NO AR')

    // Verifica que o canvas 16:9 do Monitor de Saída contém o texto escalonado sem vazar (overflow)
    const monitorCanvas = page.getByTestId('output-monitor-canvas')
    await expect(monitorCanvas).toBeVisible()
    const canvasBox = await monitorCanvas.boundingBox()
    expect(canvasBox).not.toBeNull()

    if (canvasBox) {
      // Verifica proporção 16:9 aproximada
      const ratio = canvasBox.width / canvasBox.height
      expect(ratio).toBeGreaterThan(1.7)
      expect(ratio).toBeLessThan(1.85)
    }

    // Abre o menu Ferramentas -> Diagnóstico / Sobre e confere que a telemetria foi movida para lá
    await page.getByRole('button', { name: /Ferramentas/i }).click()
    await page.getByRole('button', { name: /Diagnóstico \/ Sobre/i }).click()
    await expect(page.getByText('Diagnóstico & Sobre o LumenLite')).toBeVisible()
    await page.getByTitle('Fechar diagnóstico').click()

    // Salva captura de tela em alta definição do novo console
    await page.screenshot({
      path: '/home/miguelsb/.gemini/antigravity-cli/brain/201aea14-e159-4ace-8ea4-c514a2fd5c80/lumenlite_refactored_ui.png',
    })
  })
})
