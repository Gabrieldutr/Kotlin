package com.example.jogodavelha

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jogodavelha.databinding.ActivityMainBinding
import kotlin.random.Random

// A classe MainActivity é onde todo o nosso código do jogo da velha vai estar.
class MainActivity : AppCompatActivity() {
    // Essa variável vai guardar todos os elementos da interface (tela) que criamos no XML.
    private lateinit var binding: ActivityMainBinding

    // Criamos um tabuleiro que é uma matriz (array de arrays) de 3x3 para representar o jogo.
    private val tabuleiro = Array(3) { arrayOfNulls<String>(3) }

    // Essa variável guarda quem está jogando agora, começando pelo "X".
    private var jogadorAtual = "X"

    // Essa variável diz se estamos no modo difícil ou não. Começa como fácil (false).
    private var modoDificil = false

    // Esse método é chamado quando a atividade (tela) é criada.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aqui vinculamos (ligamos) o layout (interface gráfica) ao código.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuramos o botão de dificuldade para alternar entre fácil e difícil.
        binding.buttonDificil.setOnClickListener {
            // Alterna entre os modos fácil e difícil.
            modoDificil = !modoDificil
            if (modoDificil) {
                binding.buttonDificil.text = "Modo Difícil"
                binding.buttonDificil.setBackgroundColor(Color.RED)
            } else {
                binding.buttonDificil.text = "Modo Fácil"
                binding.buttonDificil.setBackgroundColor(Color.GREEN)
            }
        }
        // Inicializa o botão no modo fácil com a cor verde.
        binding.buttonDificil.text = "Modo Fácil"
        binding.buttonDificil.setBackgroundColor(Color.GREEN)
    }

    // Este método é chamado quando qualquer botão do tabuleiro é clicado.
    fun buttonClick(view: View) {
        val buttonSelecionado = view as Button

        // Verifica qual botão foi clicado e marca a posição no tabuleiro.
        val pos = when (buttonSelecionado.id) {
            binding.buttonZero.id -> Pair(0, 0)
            binding.buttonUm.id -> Pair(0, 1)
            binding.buttonDois.id -> Pair(0, 2)
            binding.buttonTres.id -> Pair(1, 0)
            binding.buttonQuatro.id -> Pair(1, 1)
            binding.buttonCinco.id -> Pair(1, 2)
            binding.buttonSeis.id -> Pair(2, 0)
            binding.buttonSete.id -> Pair(2, 1)
            binding.buttonOito.id -> Pair(2, 2)
            else -> return
        }

        // Marca o tabuleiro com o jogador atual ("X" ou "O").
        tabuleiro[pos.first][pos.second] = jogadorAtual
        buttonSelecionado.setBackgroundColor(Color.BLACK)
        buttonSelecionado.isEnabled = false

        // Verifica se temos um vencedor após a jogada do jogador.
        var vencedor = verificaVencedor()
        if (!vencedor.isNullOrBlank()) {
            showMessageAndRestart(vencedor)
            return
        }

        // Verifica se o tabuleiro está cheio, o que resultaria em um empate.
        if (isTabuleiroCheio()) {
            showMessageAndRestart("Empate")
            return
        }

        // Realiza a jogada do bot.
        botMove()

        // Verifica novamente se temos um vencedor após a jogada do bot.
        vencedor = verificaVencedor()
        if (!vencedor.isNullOrBlank()) {
            showMessageAndRestart(vencedor)
        }
    }

    // Este método controla a jogada do bot, dependendo do modo de dificuldade.
    private fun botMove() {
        if (modoDificil) {
            // No modo difícil, o bot escolhe a melhor jogada possível.
            val melhorMovimento = encontrarMelhorMovimento()
            tabuleiro[melhorMovimento.first][melhorMovimento.second] = "O"
            val button = getButtonAt(melhorMovimento.first, melhorMovimento.second)
            button?.setBackgroundColor(Color.RED)
            button?.isEnabled = false
        } else {
            // No modo fácil, o bot escolhe uma posição aleatória.
            while (true) {
                val rX = Random.nextInt(0, 3)
                val rY = Random.nextInt(0, 3)

                if (tabuleiro[rX][rY].isNullOrEmpty()) {
                    tabuleiro[rX][rY] = "O"
                    val button = getButtonAt(rX, rY)
                    button?.setBackgroundColor(Color.RED)
                    button?.isEnabled = false
                    break
                }
            }
        }
    }

    // Este método retorna o botão correspondente a uma posição no tabuleiro.
    private fun getButtonAt(x: Int, y: Int): Button? {
        return when (x * 3 + y) {
            0 -> binding.buttonZero
            1 -> binding.buttonUm
            2 -> binding.buttonDois
            3 -> binding.buttonTres
            4 -> binding.buttonQuatro
            5 -> binding.buttonCinco
            6 -> binding.buttonSeis
            7 -> binding.buttonSete
            8 -> binding.buttonOito
            else -> null
        }
    }

    // Este método verifica se há um vencedor no tabuleiro.
    private fun verificaVencedor(): String? {
        // Verifica todas as linhas e colunas.
        for (i in 0 until 3) {
            if (tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][1] == tabuleiro[i][2] && !tabuleiro[i][0].isNullOrEmpty()) {
                return tabuleiro[i][0]
            }
            if (tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[1][i] == tabuleiro[2][i] && !tabuleiro[0][i].isNullOrEmpty()) {
                return tabuleiro[0][i]
            }
        }
        // Verifica as diagonais.
        if (tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][2] && !tabuleiro[0][0].isNullOrEmpty()) {
            return tabuleiro[0][0]
        }
        if (tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][0] && !tabuleiro[0][2].isNullOrEmpty()) {
            return tabuleiro[0][2]
        }
        // Se ninguém ganhou, retorna nulo.
        return null
    }

    // Este método verifica se o tabuleiro está cheio, ou seja, se todas as posições estão ocupadas.
    private fun isTabuleiroCheio(): Boolean {
        for (linha in tabuleiro) {
            for (valor in linha) {
                if (valor.isNullOrEmpty()) {
                    return false
                }
            }
        }
        return true
    }

    // Este método mostra uma mensagem de quem ganhou e reinicia o jogo.
    private fun showMessageAndRestart(message: String) {
        Toast.makeText(this, "Vencedor: $message", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Este método encontra a melhor jogada possível para o bot usando o algoritmo MiniMax.
    private fun encontrarMelhorMovimento(): Pair<Int, Int> {
        var melhorValor = Int.MIN_VALUE
        var melhorMovimento = Pair(-1, -1)

        for (i in 0..2) {
            for (j in 0..2) {
                if (tabuleiro[i][j].isNullOrEmpty()) {
                    tabuleiro[i][j] = "O"
                    val movimentoValor = minimax(tabuleiro, 0, false)
                    tabuleiro[i][j] = null
                    if (movimentoValor > melhorValor) {
                        melhorMovimento = Pair(i, j)
                        melhorValor = movimentoValor
                    }
                }
            }
        }
        return melhorMovimento
    }

    // Este método usa o algoritmo MiniMax para calcular a melhor jogada.
    private fun minimax(tabuleiro: Array<Array<String?>>, profundidade: Int, isMaximizing: Boolean): Int {
        val vencedor = verificaVencedor()
        if (vencedor != null) {
            return when (vencedor) {
                "O" -> 10 - profundidade // Se o bot ganha, o valor é alto.
                "X" -> profundidade - 10 // Se o jogador ganha, o valor é baixo.
                else -> 0 // Se empatar, o valor é zero.
            }
        }

        if (isTabuleiroCheio()) {
            return 0
        }

        return if (isMaximizing) {
            var melhor = Int.MIN_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (tabuleiro[i][j].isNullOrEmpty()) {
                        tabuleiro[i][j] = "O"
                        melhor = maxOf(melhor, minimax(tabuleiro, profundidade + 1, false))
                        tabuleiro[i][j] = null
                    }
                }
            }
            melhor
        } else {
            var melhor = Int.MAX_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (tabuleiro[i][j].isNullOrEmpty()) {
                        tabuleiro[i][j] = "X"
                        melhor = minOf(melhor, minimax(tabuleiro, profundidade + 1, true))
                        tabuleiro[i][j] = null
                    }
                }
            }
            melhor
        }
    }
}