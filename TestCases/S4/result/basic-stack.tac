VTABLE<Main>:
    NULL
    "Main"

VTABLE<Stack>:
    NULL
    "Stack"
    FUNCTION<Stack.Init>
    FUNCTION<Stack.NumElems>
    FUNCTION<Stack.Pop>
    FUNCTION<Stack.Push>

FUNCTION<Main.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Main>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Stack.new>:
    _T0 = 12
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Stack>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Stack.Init>:
    _T1 = 100
    _T2 = 0
    _T3 = 1
    _T4 = (_T1 + _T3)
    _T5 = 4
    _T6 = (_T4 * _T5)
    parm _T6
    _T7 = call _Alloc
    *(_T7 + 0) = _T1
    _T8 = (_T7 + _T6)
    _T8 = (_T8 - _T5)
_L2:
    _T9 = (_T8 != _T7)
    if (_T9 == 0) branch _L1
    *(_T8 + 0) = _T2
    _T8 = (_T8 - _T5)
    branch _L2
_L1:
    _T10 = (_T7 + _T5)
    *(_T0 + 4) = _T10
    _T11 = 0
    *(_T0 + 8) = _T11
    _T12 = 3
    _T13 = *(_T0 + 0)
    _T14 = *(_T13 + 20)
    parm _T0
    parm _T12
    call _T14
    return

FUNCTION<Stack.Push>:
    _T2 = *(_T0 + 4)
    _T3 = *(_T0 + 8)
    _T4 = 4
    _T5 = (_T3 * _T4)
    _T6 = (_T2 + _T5)
    *(_T6 + 0) = _T1
    _T7 = *(_T0 + 8)
    _T8 = 1
    _T9 = (_T7 + _T8)
    *(_T0 + 8) = _T9
    return

FUNCTION<Stack.Pop>:
    _T2 = *(_T0 + 4)
    _T3 = *(_T0 + 8)
    _T4 = 1
    _T5 = (_T3 - _T4)
    _T6 = 4
    _T7 = (_T5 * _T6)
    _T8 = (_T2 + _T7)
    _T9 = *(_T8 + 0)
    _T1 = _T9
    _T10 = *(_T0 + 8)
    _T11 = 1
    _T12 = (_T10 - _T11)
    *(_T0 + 8) = _T12
    return _T1

FUNCTION<Stack.NumElems>:
    _T1 = *(_T0 + 8)
    return _T1

FUNCTION<Stack.main>:
    _T1 = call FUNCTION<Stack.new>
    _T0 = _T1
    _T2 = *(_T0 + 0)
    _T3 = *(_T2 + 8)
    parm _T0
    call _T3
    _T4 = 3
    _T5 = *(_T0 + 0)
    _T6 = *(_T5 + 20)
    parm _T0
    parm _T4
    call _T6
    _T7 = 7
    _T8 = *(_T0 + 0)
    _T9 = *(_T8 + 20)
    parm _T0
    parm _T7
    call _T9
    _T10 = 4
    _T11 = *(_T0 + 0)
    _T12 = *(_T11 + 20)
    parm _T0
    parm _T10
    call _T12
    _T13 = *(_T0 + 0)
    _T14 = *(_T13 + 12)
    parm _T0
    _T15 = call _T14
    parm _T15
    call _PrintInt
    _T16 = " "
    parm _T16
    call _PrintString
    _T17 = *(_T0 + 0)
    _T18 = *(_T17 + 16)
    parm _T0
    _T19 = call _T18
    parm _T19
    call _PrintInt
    _T20 = " "
    parm _T20
    call _PrintString
    _T21 = *(_T0 + 0)
    _T22 = *(_T21 + 16)
    parm _T0
    _T23 = call _T22
    parm _T23
    call _PrintInt
    _T24 = " "
    parm _T24
    call _PrintString
    _T25 = *(_T0 + 0)
    _T26 = *(_T25 + 16)
    parm _T0
    _T27 = call _T26
    parm _T27
    call _PrintInt
    _T28 = " "
    parm _T28
    call _PrintString
    _T29 = *(_T0 + 0)
    _T30 = *(_T29 + 12)
    parm _T0
    _T31 = call _T30
    parm _T31
    call _PrintInt
    return

main:
    call FUNCTION<Stack.main>
    return

