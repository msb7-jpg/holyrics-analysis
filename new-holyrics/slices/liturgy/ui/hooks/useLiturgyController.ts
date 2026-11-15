/**
 * @module @slices/liturgy/ui/hooks/useLiturgyController
 * @description Headless Controller para a gestão do roteiro e mídias na Coluna 1 (Esquerda) do operador.
 */

import { useState } from 'react'
import {
  useCurrentLiturgyQuery,
  useReorderLiturgyMutation,
  useAddLiturgyItemMutation,
  useRemoveLiturgyItemMutation,
  useUpdateLiturgyTitleMutation,
} from '../queries/liturgy-queries'
import type { LiturgyItem } from '../../contracts/liturgy-schema'

/**
 * Hook orquestrador do roteiro litúrgico na UI com Drag & Drop universal e menu de contexto.
 *
 * @returns Estados e manipuladores de Drag & Drop, menu de contexto e adições.
 */
export function useLiturgyController() {
  const { data: liturgy, isLoading } = useCurrentLiturgyQuery()
  const { mutate: reorderLiturgy } = useReorderLiturgyMutation()
  const { mutate: addItem } = useAddLiturgyItemMutation()
  const { mutate: removeItem } = useRemoveLiturgyItemMutation()
  const { mutate: updateTitle } = useUpdateLiturgyTitleMutation()

  const [draggedIndex, setDraggedIndex] = useState<number | null>(null)
  const [contextMenu, setContextMenu] = useState<{
    isOpen: boolean
    posX: number
    posY: number
    item?: LiturgyItem | null
  }>({
    isOpen: false,
    posX: 0,
    posY: 0,
    item: null,
  })
  const [isQuickTextOpen, setIsQuickTextOpen] = useState(false)

  const handleDragStart = (event: React.DragEvent, index: number) => {
    setDraggedIndex(index)
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/plain', String(index))
  }

  const handleDragOver = (event: React.DragEvent, _index?: number) => {
    event.preventDefault()
    event.dataTransfer.dropEffect = 'move'
  }

  const parseDroppedLumenItem = (rawPayload: string): LiturgyItem | null => {
    try {
      const parsed = JSON.parse(rawPayload) as Record<string, unknown>
      const itemId = `item-${String(parsed.type || 'custom').toLowerCase()}-${Date.now()}`
      if (parsed.type === 'SONG' && typeof parsed.songId === 'string') {
        return {
          id: itemId,
          type: 'SONG',
          title: String(parsed.title || 'Louvor'),
          songId: parsed.songId,
          artist: typeof parsed.artist === 'string' ? parsed.artist : undefined,
        }
      }
      if (parsed.type === 'BIBLE' && typeof parsed.reference === 'string') {
        return {
          id: itemId,
          type: 'BIBLE',
          title: String(parsed.title || `Leitura: ${parsed.reference}`),
          reference: parsed.reference,
          version: typeof parsed.version === 'string' ? parsed.version : 'ACF',
        }
      }
      if (parsed.type === 'MEDIA' && typeof parsed.mediaSrc === 'string') {
        return {
          id: itemId,
          type: 'MEDIA',
          title: String(parsed.title || 'Mídia'),
          mediaSrc: parsed.mediaSrc,
          mediaType: parsed.mediaType === 'VIDEO' ? 'VIDEO' : 'IMAGE',
        }
      }
      if (parsed.type === 'PPTX' && typeof parsed.fileSrc === 'string') {
        return {
          id: itemId,
          type: 'PPTX',
          title: String(parsed.title || 'Apresentação'),
          fileSrc: parsed.fileSrc,
          slidesCount: typeof parsed.slidesCount === 'number' ? parsed.slidesCount : undefined,
        }
      }
      return null
    } catch {
      return null
    }
  }

  const handleDrop = (event: React.DragEvent, dropIndex?: number) => {
    event.preventDefault()

    const customPayload = event.dataTransfer.getData('application/lumen-item')
    if (customPayload) {
      const newItem = parseDroppedLumenItem(customPayload)
      if (newItem) {
        addItem({ item: newItem, insertIndex: dropIndex })
      }
      setDraggedIndex(null)
      return
    }

    if (
      draggedIndex === null ||
      typeof dropIndex !== 'number' ||
      draggedIndex === dropIndex ||
      !liturgy
    ) {
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

  const handleOpenContextMenu = (event: React.MouseEvent, item?: LiturgyItem | null) => {
    event.preventDefault()
    event.stopPropagation()
    setContextMenu({
      isOpen: true,
      posX: event.clientX,
      posY: event.clientY,
      item: item || null,
    })
  }

  const handleCloseContextMenu = () => {
    setContextMenu((previous) => ({ ...previous, isOpen: false }))
  }

  const handleAddCountdown = () => {
    const newItem: LiturgyItem = {
      id: `item-countdown-${Date.now()}`,
      type: 'COUNTDOWN',
      title: 'Contagem Regressiva (5 min)',
      targetDurationSeconds: 300,
      description: 'Início do culto',
    }
    addItem({ item: newItem })
  }

  const handleAddNote = () => {
    const newItem: LiturgyItem = {
      id: `item-note-${Date.now()}`,
      type: 'TEXT',
      title: 'Anotação de Culto',
      content: ['Avisos e observações para a celebração.'],
      category: 'PASTORAL_NOTE',
    }
    addItem({ item: newItem })
  }

  const handleDuplicateItem = (target: LiturgyItem) => {
    const duplicated: LiturgyItem = {
      ...target,
      id: `${target.id}-copy-${Date.now()}`,
      title: `${target.title} (Cópia)`,
    }
    addItem({ item: duplicated })
  }

  const handleMoveItemRelative = (target: LiturgyItem, delta: -1 | 1) => {
    if (!liturgy) return
    const items = [...liturgy.items]
    const currentIndex = items.findIndex((entry) => entry.id === target.id)
    if (currentIndex < 0) return
    const nextIndex = currentIndex + delta
    if (nextIndex < 0 || nextIndex >= items.length) return

    const [moved] = items.splice(currentIndex, 1)
    items.splice(nextIndex, 0, moved)
    reorderLiturgy(items.map((entry) => entry.id))
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
    handleDuplicateItem,
    handleMoveItemUp: (target: LiturgyItem) => handleMoveItemRelative(target, -1),
    handleMoveItemDown: (target: LiturgyItem) => handleMoveItemRelative(target, 1),
    handleAddCustomItem: (item: LiturgyItem, insertIndex?: number) => addItem({ item, insertIndex }),
    handleRemoveItem: (id: string) => removeItem(id),
    handleUpdateTitle: (title: string) => updateTitle(title),
  }
}
