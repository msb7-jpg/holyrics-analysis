/**
 * @module @slices/liturgy/ui/components/QuickTextModal
 * @description Modal compacto de entrada rápida de texto livre de emergência (RF-22A).
 *
 * ## Responsabilidades:
 * - Digitar avisos urgentes ou notas pastorais (placas de carro, recados, oração).
 * - Lançar imediatamente na projeção ativa sem alterar o banco de músicas.
 * - Suporte a atalho Ctrl+Enter para envio instantâneo.
 */

import { useState } from 'react'
import { MessageSquareText, Send, X } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Textarea } from '@ui/components/ui/textarea'
import { useQuickTextMutation } from '../queries/liturgy-queries'

export interface QuickTextModalProps {
  isOpen: boolean
  onClose: () => void
}

/**
 * Diálogo de texto avulso de emergência.
 *
 * @param props - Propriedades do diálogo.
 * @returns Elemento JSX do modal.
 */
export function QuickTextModal({ isOpen, onClose }: QuickTextModalProps) {
  const [title, setTitle] = useState('Aviso Importante')
  const [text, setText] = useState('')
  const [category, setCategory] = useState<'ALERT' | 'ANNOUNCEMENT' | 'PASTORAL_NOTE'>('ALERT')

  const { mutate: sendQuickText, isPending } = useQuickTextMutation()

  if (!isOpen) return null

  const handleSend = () => {
    if (!text.trim()) return

    sendQuickText(
      {
        title: title.trim() || 'Aviso Rápido',
        text: text.trim(),
        category,
        broadcastOnly: false,
      },
      {
        onSuccess: () => {
          setText('')
          onClose()
        },
      }
    )
  }

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
      event.preventDefault()
      handleSend()
    } else if (event.key === 'Escape') {
      event.preventDefault()
      onClose()
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-xs p-4">
      <div
        className="w-full max-w-md rounded-xl border border-white/10 bg-slate-900 p-5 shadow-2xl flex flex-col gap-4 text-slate-100"
        onKeyDown={handleKeyDown}
      >
        <div className="flex items-center justify-between border-b border-white/10 pb-3">
          <div className="flex items-center gap-2">
            <MessageSquareText className="h-5 w-5 text-sky-400" />
            <h3 className="text-sm font-semibold text-white">Projeção Rápida de Texto (Quick Text)</h3>
          </div>
          <button
            onClick={onClose}
            className="rounded p-1 text-slate-400 hover:bg-slate-800 hover:text-white transition"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <div className="flex flex-col gap-3 text-xs">
          <div>
            <label className="text-[11px] font-medium text-slate-400 mb-1 block">Título do Slide</label>
            <Input
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="Ex: Veículo no Estacionamento"
              className="bg-slate-950/60"
            />
          </div>

          <div>
            <label className="text-[11px] font-medium text-slate-400 mb-1 block">Conteúdo do Recado</label>
            <Textarea
              rows={4}
              value={text}
              onChange={(event: React.ChangeEvent<HTMLTextAreaElement>) => setText(event.target.value)}
              placeholder="Digite o aviso que será exibido no telão imediatamente..."
              className="bg-slate-950/60 text-xs"
              autoFocus
            />
            <span className="text-[10px] text-slate-500 mt-1 block">
              Dica: Linhas separadas formam parágrafos no slide. Pressione Ctrl+Enter para projetar.
            </span>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-[11px] text-slate-400">Tipo:</span>
            <button
              type="button"
              onClick={() => setCategory('ALERT')}
              className={`px-2.5 py-1 rounded text-[11px] font-medium transition ${
                category === 'ALERT'
                  ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                  : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
            >
              Urgência
            </button>
            <button
              type="button"
              onClick={() => setCategory('ANNOUNCEMENT')}
              className={`px-2.5 py-1 rounded text-[11px] font-medium transition ${
                category === 'ANNOUNCEMENT'
                  ? 'bg-sky-500/20 text-sky-300 border border-sky-500/30'
                  : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
            >
              Aviso Geral
            </button>
            <button
              type="button"
              onClick={() => setCategory('PASTORAL_NOTE')}
              className={`px-2.5 py-1 rounded text-[11px] font-medium transition ${
                category === 'PASTORAL_NOTE'
                  ? 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/30'
                  : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
            >
              Pastoral
            </button>
          </div>
        </div>

        <div className="flex items-center justify-end gap-2 border-t border-white/10 pt-3">
          <Button variant="ghost" size="sm" onClick={onClose} disabled={isPending}>
            Cancelar
          </Button>
          <Button
            size="sm"
            onClick={handleSend}
            disabled={!text.trim() || isPending}
            className="bg-sky-500 hover:bg-sky-600 text-white gap-1.5"
          >
            <Send className="h-3.5 w-3.5" />
            Lançar no Telão
          </Button>
        </div>
      </div>
    </div>
  )
}
