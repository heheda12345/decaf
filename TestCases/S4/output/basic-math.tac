VTABLE<Main>:
    NULL
    "Main"

VTABLE<Maths>:
    NULL
    "Maths"

FUNCTION<Maths.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Maths>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Main.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Main>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Maths.abs>:
    _T1 = 0
    _T2 = (_T0 >= _T1)
    if (_T2 == 0) branch _L1
    return _T0
    branch _L2
_L1:
    _T3 = - _T0
    return _T3
_L2:
    return

FUNCTION<Maths.pow>:
    _T3 = 1
    _T2 = _T3
    _T5 = 0
    _T4 = _T5
_L4:
    _T6 = (_T4 < _T1)
    if (_T6 == 0) branch _L3
    _T7 = (_T2 * _T0)
    _T2 = _T7
    _T8 = 1
    _T9 = (_T4 + _T8)
    _T4 = _T9
    branch _L4
_L3:
    return _T2

FUNCTION<Maths.log>:
    _T1 = 1
    _T2 = (_T0 < _T1)
    if (_T2 == 0) branch _L5
    _T3 = 1
    _T4 = - _T3
    return _T4
_L5:
    _T6 = 0
    _T5 = _T6
_L7:
    _T7 = 1
    _T8 = (_T0 > _T7)
    if (_T8 == 0) branch _L6
    _T9 = 1
    _T10 = (_T5 + _T9)
    _T5 = _T10
    _T11 = 2
    _T12 = (_T0 / _T11)
    _T0 = _T12
    branch _L7
_L6:
    return _T5

FUNCTION<Maths.max>:
    _T2 = (_T0 > _T1)
    if (_T2 == 0) branch _L8
    return _T0
    branch _L9
_L8:
    return _T1
_L9:
    return

FUNCTION<Maths.min>:
    _T2 = (_T0 < _T1)
    if (_T2 == 0) branch _L10
    return _T0
    branch _L11
_L10:
    return _T1
_L11:
    return

main:
    _T0 = 1
    _T1 = - _T0
    parm _T1
    _T2 = call FUNCTION<Maths.abs>
    parm _T2
    call _PrintInt
    _T3 = "\n"
    parm _T3
    call _PrintString
    _T4 = 2
    _T5 = 3
    parm _T4
    parm _T5
    _T6 = call FUNCTION<Maths.pow>
    parm _T6
    call _PrintInt
    _T7 = "\n"
    parm _T7
    call _PrintString
    _T8 = 16
    parm _T8
    _T9 = call FUNCTION<Maths.log>
    parm _T9
    call _PrintInt
    _T10 = "\n"
    parm _T10
    call _PrintString
    _T11 = 1
    _T12 = 2
    parm _T11
    parm _T12
    _T13 = call FUNCTION<Maths.max>
    parm _T13
    call _PrintInt
    _T14 = "\n"
    parm _T14
    call _PrintString
    _T15 = 1
    _T16 = 2
    parm _T15
    parm _T16
    _T17 = call FUNCTION<Maths.min>
    parm _T17
    call _PrintInt
    _T18 = "\n"
    parm _T18
    call _PrintString
    return

