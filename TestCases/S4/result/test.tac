VTABLE<Main>:
    NULL
    "Main"
    FUNCTION<Main.call1>

FUNCTION<Main.new>:
    _T0 = 8
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Main>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Main.call1>:
    _T1 = 1
    return _T1

FUNCTION<Main.call2>:
    _T0 = 2
    return _T0

FUNCTION<Main.call>:
    _T2 = *(_T0 + 0)
    _T3 = *(_T2 + 8)
    parm _T0
    _T4 = call _T3
    _T1 = _T4
    _T6 = call FUNCTION<Main.call2>
    _T5 = _T6
    _T8 = *(_T0 + 4)
    _T7 = _T8
    _T9 = _T7
    _T10 = _T9
    _T12 = "abcd"
    _T11 = _T12
    _T14 = 1
    _T13 = _T14
    _T16 = ! _T13
    _T15 = _T16
    _T18 = (_T9 + _T10)
    _T17 = _T18
    return

main:
    _T1 = call FUNCTION<Main.new>
    _T0 = _T1
    parm _T0
    call FUNCTION<Main.call>
    return

