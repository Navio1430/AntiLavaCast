package pl.spcode.antilavacast.paper.listener

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFormEvent

class BlockListener: Listener {

  @EventHandler
  fun onBlockFormEvent(event: BlockFormEvent) {
    val location = event.block.location
    val threshold = 5

    if (event.block.type == Material.LAVA && event.newState.type == Material.COBBLESTONE) {
      if (shouldBlockRecursive(location.block, threshold)) {
        event.isCancelled = true
        clearLavaUnderRecursive(location.block)
        clearWaterAboveRecursive(location.block)
        return
      }
    }
  }

  // todo add to block removal queue
  fun clearLavaUnderRecursive(block: Block) {
    getLavaBlocksUnder(block).forEach {
      it.type = Material.AIR
      clearLavaUnderRecursive(it)
    }
  }

  fun clearWaterAboveRecursive(block: Block) {
    getWaterBlocksAround(block).forEach {
      it.type = Material.AIR
      clearWaterAboveRecursive(it)
    }
  }

  fun getWaterBlocksAround(block: Block): List<Block> {
    val blocks = mutableListOf<Block>()

    for (x in -1..1) {
      for (z in -1..1) {
        for (y in 0..1) {
          val newX = block.x + x
          val newZ = block.z + z
          val newY = block.y + y
          val block = block.world.getBlockAt(newX, newY, newZ)
          if (block.type == Material.WATER) {
            blocks.add(block)
          }
        }
      }
    }

    return blocks
  }

  fun getLavaBlocksUnder(block: Block): List<Block> {
    val blocks = mutableListOf<Block>()

    for (x in -1..1) {
      for (z in -1..1) {
        for (y in -1..0) {
          val newX = block.x + x
          val newZ = block.z + z
          val newY = block.y + y
          val block = block.world.getBlockAt(newX, newY, newZ)
          if (block.type == Material.LAVA) {
            blocks.add(block)
          }
        }
      }
    }

    return blocks
  }

  fun shouldBlockRecursive(block: Block, remainingSize: Int): Boolean {
    if (remainingSize <= 0) return true

    val location = block.location
    val y = location.blockY
    val w = location.world

    val blockUp = w.getBlockAt(location.blockX, y + 1, location.blockZ)
    if (isCobblestone(blockUp)) {
      val res = shouldBlockRecursive(blockUp, remainingSize - 1)
      return res
    } else {
      // iterate sideways
      val blocks = getCobblestonesOnUpperSides(block)
      blocks.forEach {
        if (shouldBlockRecursive(it, remainingSize - 1)) {
          return true
        }
      }
      if (blocks.isEmpty()) return false
    }

    return false
  }

  fun isCobblestone(block: Block): Boolean {
    return block.type == Material.COBBLESTONE
  }

  fun getCobblestonesOnUpperSides(block: Block): List<Block> {
    val newY = block.y + 1
    val w = block.world

    val blocks = mutableListOf<Block>()
    for (x in intArrayOf(-1, 1)) {
      for (z in intArrayOf(-1, 1)) {
        val newX = block.x + x
        val newZ = block.z + z
        val block = w.getBlockAt(newX, newY, newZ)
        if (isCobblestone(block)) {
          blocks.add(block)
        }
      }
    }

    return blocks
  }

}