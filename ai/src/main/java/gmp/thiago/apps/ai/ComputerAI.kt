package gmp.thiago.apps.ai

class ComputerAI {
    companion object {
        fun getComputerMove(board : Array<Char>) : Int{

            var choice: Int

            do {
                choice = (Math.random() * board.size).toInt()
            } while (board[choice] != ' ')
            return choice
        }
    }

}