VTABLE<Fibonacci>:
    NULL
    "Fibonacci"
    FUNCTION<Fibonacci.get>

VTABLE<Main>:
    NULL
    "Main"

FUNCTION<Fibonacci.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Fibonacci>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Main.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Main>
    *(_T1 + 0) = _T2
    return _T1

main:
    _T1 = 0
    _T0 = _T1
    _T3 = call FUNCTION<Fibonacci.new>
    _T2 = _T3
_L2:
    _T4 = 10
    _T5 = (_T0 < _T4)
    if (_T5 == 0) branch _L1
    _T6 = *(_T2 + 0)
    _T7 = *(_T6 + 8)
    parm _T2
    parm _T0
    _T8 = call _T7
    parm _T8
    call _PrintInt
    _T9 = "\n"
    parm _T9
    call _PrintString
    _T10 = 1
    _T11 = (_T0 + _T10)
    _T0 = _T11
    branch _L2
_L1:
    return

FUNCTION<Fibonacci.get>:
    _T2 = 2
    _T3 = (_T1 < _T2)
    if (_T3 == 0) branch _L3
    _T4 = 1
    return _T4
_L3:
    _T5 = 1
    _T6 = (_T1 - _T5)
    _T7 = *(_T0 + 0)
    _T8 = *(_T7 + 8)
    parm _T0
    parm _T6
    _T9 = call _T8
    _T10 = 2
    _T11 = (_T1 - _T10)
    _T12 = *(_T0 + 0)
    _T13 = *(_T12 + 8)
    parm _T0
    parm _T11
    _T14 = call _T13
    _T15 = (_T9 + _T14)
    return _T15

