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
    call _T3
    call FUNCTION<Main.call2>
    return

main:
    _T1 = call FUNCTION<Main.new>
    _T0 = _T1
    parm _T0
    call FUNCTION<Main.call>
    return

