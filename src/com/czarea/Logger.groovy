package com.czarea

/**
 * 日志记录
 *
 * @author zhouzx
 */
class Logger implements Serializable {

    static void logger(value, color) {
        def colors = ['red'   : "\033[40;31m ${value} \033[0m",
                      'blue'  : "\033[47;34m ${value} \033[0m",
                      'green' : "[1;32m ${value}[m",
                      'green1': "\033[40;32m ${value} \033[0m"]
        ansiColor('xterm') {
            println(colors[color])
        }
    }
}
