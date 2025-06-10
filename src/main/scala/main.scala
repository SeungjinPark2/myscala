import akka.actor.typed.ActorSystem
import indexer.{BlockEvent, IndexerActor, Profile}

// actor sample
@main
def main(): Unit = {
  val system = ActorSystem(IndexerActor(), "IndexerSystem")

  val profile1 = Profile("user1", "0xabc", "0xdef")
  val profile2 = Profile("user2", "0xaaa", "0xbbb")

  system ! IndexerActor.AddProfile(profile1)
  system ! IndexerActor.AddProfile(profile2)

  val blockEvents = List(
    BlockEvent("0xabc", "0xdef", 100),
    BlockEvent("0xaaa", "0xccc", 200),
    BlockEvent("0xabc", "0xdef", 300)
  )

  system ! IndexerActor.ParseBlock(blockEvents)

  Thread.sleep(2000)
  system.terminate()
}
