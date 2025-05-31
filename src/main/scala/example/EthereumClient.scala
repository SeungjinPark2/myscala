package example

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService

class EthereumClient(private val web3: Web3j) {
  def this(endpoint: String) = {
    this(Web3j.build(new HttpService(endpoint)))
  }

  def getClientVersion: Web3ClientVersion = {
    web3.web3ClientVersion().send()
  }

  def printBlockInfo(blockNumber: BigInt): Unit = {
    val param = DefaultBlockParameter.valueOf(new java.math.BigInteger(blockNumber.toString))
    val response = web3.ethGetBlockByNumber(param, false).send() // false = 트랜잭션 상세정보 제외

    val block = response.getBlock

    println(s"📦 Block Number: ${block.getNumber}")
    println(s"🔗 Hash: ${block.getHash}")
    println(s"⛏️  Miner: ${block.getMiner}")
    println(s"📅 Timestamp: ${block.getTimestamp}")
    println(s"📄 Tx Count: ${block.getTransactions.size()}")
    println(s"⛽ Gas Used: ${block.getGasUsed}")
    println(s"⛽ Gas Limit: ${block.getGasLimit}")
  }
}
