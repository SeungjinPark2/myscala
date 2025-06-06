package example

object EthereumClientSmokeTest extends App {
  val endpoint = "https://eth.llamarpc.com"
  val client = new EthereumClient(endpoint)

  val version = client.getClientVersion
  println(s"Web3 Client Version: $version")

  client.printBlockInfo(22603690)
  client.printTransactionInfo("0xdd82f25f166eca3414f33787e848879fe9c641eceb281aedbe43b8eac1b5008f")
}
