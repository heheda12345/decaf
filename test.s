    # start of header
    .text
    .globl main
    # end of header

    .data
    .align 2
_V_Main:  # virtual table for Main
    .word 0    # parent: none
    .word _S0    # class name

    .text
_L_Main_new:  # function FUNCTION<Main.new>
    # start of prologue
    addiu   $sp, $sp, -36  # push stack frame
    sw      $ra, 32($sp)  # save the return address
    # end of prologue

    # start of body
    li      $v1, 4
    move    $a0, $v1
    li      $v0, 9
    syscall
    move    $v1, $v0
    la      $t0, _V_Main
    sw      $t0, 0($v1)
    move    $v0, $v1
    j       _L_Main_new_exit
    # end of body

_L_Main_new_exit:
    # start of epilogue
    lw      $ra, 32($sp)  # restore the return address
    addiu   $sp, $sp, 36  # pop stack frame
    # end of epilogue

    jr      $ra  # return

main:  # function main
    # start of prologue
    addiu   $sp, $sp, -36  # push stack frame
    # end of prologue

    # start of body
    j       main_exit
    # end of body

main_exit:
    # start of epilogue
    addiu   $sp, $sp, 36  # pop stack frame
    # end of epilogue

    jr      $ra  # return

    # start of constant strings
    .data
_S0:
    .asciiz "Main"
    # end of constant strings
