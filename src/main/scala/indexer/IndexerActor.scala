package indexer

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object IndexerActor {
  sealed trait Command
  final case class AddProfile(profile: Profile) extends Command
  final case class ParseBlock(events: List[BlockEvent]) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      var profiles = List.empty[Profile]

      Behaviors.receiveMessage {
        case AddProfile(profile) =>
          context.log.info(s"📦 Profile registered: $profile")
          profiles = profile :: profiles
          Behaviors.same

        case ParseBlock(events) =>
          context.log.info(s"🔍 Parsing ${events.size} events...")
          for
            event <- events
            matched <- profiles.find(p => p.from == event.from && p.to == event.to)
          do context.log.info(s"✅ Event matched for Profile[${matched.id}]: $event")

          Behaviors.same
      }
    }
}
