package gdev.generate.dece

import kotlinx.coroutines.*
import org.web3j.abi.datatypes.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import java.util.*

data class ModuleConfig(
    val moduleName: String,
    val moduleAddress: String,
    val abi: String,
    val contractAddress: String,
    val gasPrice: Uint256,
    val gasLimit: Uint256
)

data class MonitorConfig(
    val ARVRModules: List<ModuleConfig>,
    val pollingInterval: Long,
    val web3jUrl: String,
    val chainId: Uint256
)

object DecentralizedARVRModuleMonitor {
    private val monitorConfig: MonitorConfig by lazy {
        MonitorConfig(
            ARVRModules = listOf(
                ModuleConfig(
                    moduleName = "exampleModule1",
                    moduleAddress = "0x123456...",
                    abi = "...",
                    contractAddress = "0x987654...",
                    gasPrice = Uint256("20000000000"),
                    gasLimit = Uint256("50000")
                ),
                ModuleConfig(
                    moduleName = "exampleModule2",
                    moduleAddress = "0x345678...",
                    abi = "...",
                    contractAddress = "0x234567...",
                    gasPrice = Uint256("20000000000"),
                    gasLimit = Uint256("50000")
                )
            ),
            pollingInterval = 10000,
            web3jUrl = "https://mainnet.infura.io/v3/YOUR_PROJECT_ID",
            chainId = Uint256("1")
        )
    }

    private val web3j: Web3j by lazy {
        Web3j.build(HttpService(monitorConfig.web3jUrl))
    }

    fun startMonitoring() = runBlocking {
        while (true) {
            monitorConfig.ARVRModules.forEach { module ->
                val transactionReceipt: TransactionReceipt = web3j.getTransactionReceipt(module.contractAddress).send()
                // process transaction receipt
                println("Module ${module.moduleName} status: ${transactionReceipt.status}")
            }
            delay(monitorConfig.pollingInterval)
        }
    }
}

fun main() {
    DecentralizedARVRModuleMonitor.startMonitoring()
}