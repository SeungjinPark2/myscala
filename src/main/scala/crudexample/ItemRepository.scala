package crudexample

import scala.collection.concurrent.TrieMap

object ItemRepository {
  // In-memory store
  private val store = new TrieMap[Int, Item]()
  private val idGenerator = new java.util.concurrent.atomic.AtomicInteger(0)

  def all(): List[Item] = store.values.toList

  def get(id: Int): Option[Item] = store.get(id)

  def create(name: String, description: String): Item = {
    val id = idGenerator.incrementAndGet()
    val item = Item(id, name, description)
    store.put(id, item)
    item
  }

  def update(id: Int, name: String, description: String): Option[Item] = {
    store.get(id).map { _ =>
      val updated = Item(id, name, description)
      store.put(id, updated)
      updated
    }
  }

  def delete(id: Int): Boolean = store.remove(id).isDefined
}
