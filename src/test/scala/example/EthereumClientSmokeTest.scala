package example

object EthereumClientSmokeTest extends App {
  val endpoint = "https://eth.llamarpc.com"
  val client = new EthereumClient(endpoint)

  val version = client.getClientVersion
  println(s"Web3 Client Version: $version")

  client.printBlockInfo(22603690)
}
