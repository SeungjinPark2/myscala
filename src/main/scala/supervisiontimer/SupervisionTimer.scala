package supervisiontimer

import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.DurationInt

object SupervisionTimer {
  // ----- 메시지 정의 -----
  sealed trait Command
  case object Tick extends Command
  case object Fail extends Command

  // ----- TimerActor 정의 -----
  object TimerActor {
    def apply(): Behavior[Command] = Behaviors.withTimers { timers =>
      timers.startTimerAtFixedRate(Tick, 1.second)

      Behaviors.receive { (context, message) =>
        message match {
          case Tick =>
            context.log.info("Tick received")
            Behaviors.same

          case Fail =>
            context.log.warn("About to fail!")
            throw new RuntimeException("Simulated failure")
        }
      }
    }
  }

  // ----- Supervisor Actor 정의 -----
  object Guardian {
    def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      val supervised = context.spawn(
        Behaviors.supervise(TimerActor())
          .onFailure[RuntimeException](SupervisorStrategy.restart),
        name = "supervisedTimer"
      )

      // 5초 후에 Fail 메시지 전송
      context.scheduleOnce(5.seconds, supervised, Fail)

      Behaviors.empty
    }
  }

  // ----- 메인 실행 -----
  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](Guardian(), "SupervisionTimerSystem")
  }
}
