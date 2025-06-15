package bankaccount

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

object BankAccountActorExample {
  // ----- Command Protocol 정의 -----
  // sealed trait 은 명확한 메시지 타입 계층을 제한하는 데 사용.
  // T 타입의 메시지를 받을 수 있는 액터의 참조(주소).
  sealed trait Command

  // ActorRef 는 T 타입의 메세지를 처리하는 Actor 의 참조 주소를 뜻
  final case class Deposit(amount: Int, replyTo: ActorRef[OperationResult]) extends Command
  final case class Withdraw(amount: Int, replyTo: ActorRef[OperationResult]) extends Command
  final case class GetBalance(replyTo: ActorRef[Balance]) extends Command

  // ----- 응답 타입 정의 -----
  sealed trait OperationResult

  final case class Accepted(newBalance: Int) extends OperationResult
  final case class Rejected(reason: String) extends OperationResult

  final case class Balance(amount: Int)

  // ----- Actor Behavior 정의 -----
  object BankAccount {
    def apply(): Behavior[Command] = behavior(0) // 초기 잔액 0부터 시작

    private def behavior(balance: Int): Behavior[Command] = Behaviors.receive { (context, message) =>
      message match {
        case Deposit(amount, replyTo) =>
          val newBalance = balance + amount
          context.log.info(s"Deposited $amount, new balance: $newBalance")
          replyTo ! Accepted(newBalance)
          behavior(newBalance)

        case Withdraw(amount, replyTo) =>
          if (amount > balance) {
            context.log.info(s"Withdrawal of $amount rejected, current balance: $balance")
            replyTo ! Rejected("Insufficient funds")
            Behaviors.same
          } else {
            val newBalance = balance - amount
            context.log.info(s"Withdrew $amount, new balance: $newBalance")
            replyTo ! Accepted(newBalance)
            behavior(newBalance)
          }

        case GetBalance(replyTo) =>
          replyTo ! Balance(balance)
          Behaviors.same
      }
    }
  }

  // ----- 테스트용 Guardian Actor -----
  object Main {
    def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      val account = context.spawn(BankAccount(), "bankAccount")

      // 결과 출력용 Actor
      val printer = context.spawn(Printer(), "printer")

      // 테스트 명령 전송
      account ! Deposit(100, printer)
      account ! Withdraw(40, printer)
      account ! Withdraw(100, printer)
      account ! GetBalance(printer)

      Behaviors.empty
    }
  }

  // ----- 결과 출력 Actor -----
  object Printer {
    def apply(): Behavior[Any] = Behaviors.receive { (context, message) =>
      context.log.info(s"[Response] $message")
      Behaviors.same
    }
  }

  // ----- 메인 실행 -----
  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](Main(), "BankAccountSystem")
  }
}
