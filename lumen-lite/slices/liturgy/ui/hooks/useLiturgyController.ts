/**
 * @module @slices/liturgy/ui/hooks/useLiturgyController
 * @description Headless Controller para a gestão da liturgia na Coluna 1 do operador.
 *
 * ## Responsabilidades:
 * - Carregar a liturgia ativa via TanStack Query.
 * - Gerenciar operações de Drag & Drop nativo (reordenação de itens sem dependências pesadas).
 * - Gerenciar menu de contexto (clique com o botão direito).
 * - Ações de inserção de contagem, anotação ou Quick Text.
 */

import { useState } from 'react'
import {
  useCurrentLiturgyQuery,
  useReorderLiturgyMutation,
  useAddLiturgyItemMutation,
  useRemoveLiturgyItemMutation,
} from '../queries/liturgy-queries'
import type { LiturgyItem } from '../../contracts/liturgy-schema'

/**
 * Hook orquestrador do roteiro litúrgico na UI.
 *
 * @returns Estados e manipuladores de Drag & Drop, context menu e adições.
 */
export function useLiturgyController() {
  const { data: liturgy, isLoading } = useCurrentLiturgyQuery()
  const { mutate: reorderLiturgy } = useReorderLiturgyMutation()
  const { mutate: addItem } = useAddLiturgyItemMutation()
  const { mutate: removeItem } = useRemoveLiturgyItemMutation()

  // Estados de Drag & Drop
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null)

  // Estados do Menu de Contexto
  const [contextMenu, setContextMenu] = useState<{
    isOpen: boolean
    x: number
    y: number
    item?: LiturgyItem | null
  }>({
    isOpen: false,
    x: 0,
    y: 0,
    item: null,
  })

  // Modal de Quick Text
  const [isQuickTextOpen, setIsQuickTextOpen] = useState(false)

  // Handlers de Drag & Drop
  const handleDragStart = (event: React.DragEvent, index: number) => {
    setDraggedIndex(index)
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', String(index))
  }

  const handleDragOver = (event: React.DragEvent, _index: number) => {
    event.preventDefault()
    event.dataTransfer.dropEffect = 'move'
  }

  const handleDrop = (event: React.DragEvent, dropIndex: number) => {
    event.preventDefault()
    if (draggedIndex === null || draggedIndex === dropIndex || !liturgy) {
      setDraggedIndex(null)
      return
    }

    const items = [...liturgy.items]
    const [moved] = items.splice(draggedIndex, 1)
    items.splice(dropIndex, 0, moved)

    const reorderedIds = items.map((item) => item.id)
    reorderLiturgy(reorderedIds)
    setDraggedIndex(null)
  }

  // Handlers de Context Menu
  const handleOpenContextMenu = (event: React.MouseEvent, item?: LiturgyItem | null) => {
    event.preventDefault()
    setContextMenu({
      isOpen: true,
      x: event.clientX,
      y: event.clientY,
      item: item || null,
    })
  }

  const handleCloseContextMenu = () => {
    setContextMenu((prev) => ({ ...prev, isOpen: false }))
  }

  // Ações de criação rápida
  const handleAddCountdown = () => {
    const newItem: LiturgyItem = {
      id: `item-countdown-${Date.now()}`,
      type: 'COUNTDOWN',
      title: 'Contagem Regressiva 5min',
      targetDurationSeconds: 300,
      description: 'Início do culto',
    }
    addItem({ item: newItem })
  }

  const handleAddNote = () => {
    const newItem: LiturgyItem = {
      id: `item-note-${Date.now()}`,
      type: 'TEXT',
      title: 'Anotação Pastoral',
      content: ['Anotações para a ministração do culto.'],
      category: 'PASTORAL_NOTE',
    }
    addItem({ item: newItem })
  }

  return {
    liturgy,
    isLoading,
    draggedIndex,
    contextMenu,
    isQuickTextOpen,
    setIsQuickTextOpen,
    handleDragStart,
    handleDragOver,
    handleDrop,
    handleOpenContextMenu,
    handleCloseContextMenu,
    handleAddCountdown,
    handleAddNote,
    handleRemoveItem: (id: string) => removeItem(id),
  }
}
