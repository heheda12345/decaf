VTABLE<Main>:
    NULL
    "Main"

VTABLE<Node>:
    NULL
    "Node"

VTABLE<RBTree>:
    VTABLE<Node>
    "RBTree"
    FUNCTION<RBTree.delete>
    FUNCTION<RBTree.delete_fix>
    FUNCTION<RBTree.insert>
    FUNCTION<RBTree.insert_fix>
    FUNCTION<RBTree.print>
    FUNCTION<RBTree.print_impl>
    FUNCTION<RBTree.rotate>
    FUNCTION<RBTree.transplant>

VTABLE<Rng>:
    NULL
    "Rng"
    FUNCTION<Rng.next>

FUNCTION<Node.new>:
    _T0 = 24
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Node>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<RBTree.new>:
    _T0 = 32
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<RBTree>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Rng.new>:
    _T0 = 8
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Rng>
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
    _T1 = 19260817
    parm _T1
    _T2 = call FUNCTION<Rng.make>
    _T0 = _T2
    _T4 = call FUNCTION<RBTree.make1>
    _T3 = _T4
    _T6 = 0
    _T5 = _T6
_L2:
    _T7 = 5
    _T8 = (_T5 < _T7)
    if (_T8 == 0) branch _L1
    _T10 = 0
    _T9 = _T10
_L4:
    _T11 = 500
    _T12 = (_T9 < _T11)
    if (_T12 == 0) branch _L3
    _T13 = *(_T0 + 0)
    _T14 = *(_T13 + 8)
    parm _T0
    _T15 = call _T14
    _T16 = 500
    _T17 = (_T15 % _T16)
    _T18 = *(_T3 + 0)
    _T19 = *(_T18 + 16)
    parm _T3
    parm _T17
    call _T19
    _T20 = 1
    _T21 = (_T9 + _T20)
    _T9 = _T21
    branch _L4
_L3:
    _T23 = 0
    _T22 = _T23
_L6:
    _T24 = 500
    _T25 = (_T22 < _T24)
    if (_T25 == 0) branch _L5
    _T26 = *(_T0 + 0)
    _T27 = *(_T26 + 8)
    parm _T0
    _T28 = call _T27
    _T29 = 500
    _T30 = (_T28 % _T29)
    _T31 = *(_T3 + 0)
    _T32 = *(_T31 + 8)
    parm _T3
    parm _T30
    call _T32
    _T33 = 1
    _T34 = (_T22 + _T33)
    _T22 = _T34
    branch _L6
_L5:
    _T35 = 1
    _T36 = (_T5 + _T35)
    _T5 = _T36
    branch _L2
_L1:
    _T37 = *(_T3 + 0)
    _T38 = *(_T37 + 24)
    parm _T3
    call _T38
    return

FUNCTION<Rng.make>:
    _T2 = call FUNCTION<Rng.new>
    _T1 = _T2
    *(_T1 + 4) = _T0
    return _T1

FUNCTION<Rng.next>:
    _T1 = 15625
    _T2 = *(_T0 + 4)
    _T3 = 10000
    _T4 = (_T2 % _T3)
    _T5 = (_T1 * _T4)
    _T6 = 22221
    _T7 = (_T5 + _T6)
    _T8 = 65536
    _T9 = (_T7 % _T8)
    *(_T0 + 4) = _T9
    _T10 = *(_T0 + 4)
    return _T10

FUNCTION<Node.make>:
    _T4 = call FUNCTION<Node.new>
    _T3 = _T4
    *(_T3 + 8) = _T2
    *(_T3 + 16) = _T0
    *(_T3 + 12) = _T1
    *(_T3 + 20) = _T1
    _T5 = 1
    *(_T3 + 4) = _T5
    return _T3

FUNCTION<RBTree.make1>:
    _T1 = call FUNCTION<RBTree.new>
    _T0 = _T1
    _T3 = call FUNCTION<Node.new>
    _T2 = _T3
    *(_T2 + 16) = _T2
    *(_T2 + 12) = _T2
    *(_T2 + 20) = _T2
    *(_T0 + 28) = _T2
    *(_T0 + 24) = _T2
    return _T0

FUNCTION<RBTree.transplant>:
    _T4 = *(_T1 + 16)
    _T3 = _T4
    _T5 = *(_T0 + 24)
    _T6 = (_T3 == _T5)
    if (_T6 == 0) branch _L7
    *(_T0 + 28) = _T2
    branch _L8
_L7:
    _T7 = *(_T3 + 20)
    _T8 = (_T7 == _T1)
    if (_T8 == 0) branch _L9
    *(_T3 + 20) = _T2
    branch _L10
_L9:
    *(_T3 + 12) = _T2
_L10:
_L8:
    *(_T2 + 16) = _T3
    return

FUNCTION<RBTree.rotate>:
    _T3 = *(_T1 + 16)
    _T2 = _T3
    _T5 = *(_T2 + 16)
    _T4 = _T5
    *(_T1 + 16) = _T4
    _T6 = *(_T0 + 24)
    _T7 = (_T4 == _T6)
    if (_T7 == 0) branch _L11
    *(_T0 + 28) = _T1
    branch _L12
_L11:
    _T8 = *(_T4 + 20)
    _T9 = (_T8 == _T2)
    if (_T9 == 0) branch _L13
    *(_T4 + 20) = _T1
    branch _L14
_L13:
    *(_T4 + 12) = _T1
_L14:
_L12:
    _T10 = *(_T2 + 12)
    _T11 = (_T10 == _T1)
    if (_T11 == 0) branch _L15
    _T12 = *(_T1 + 20)
    *(_T2 + 12) = _T12
    _T13 = *(_T1 + 20)
    *(_T13 + 16) = _T2
    *(_T1 + 20) = _T2
    branch _L16
_L15:
    _T14 = *(_T1 + 12)
    *(_T2 + 20) = _T14
    _T15 = *(_T1 + 12)
    *(_T15 + 16) = _T2
    *(_T1 + 12) = _T2
_L16:
    *(_T2 + 16) = _T1
    return

FUNCTION<RBTree.insert_fix>:
_L18:
    _T2 = *(_T1 + 16)
    _T3 = *(_T2 + 4)
    if (_T3 == 0) branch _L17
    _T5 = *(_T1 + 16)
    _T4 = _T5
    _T7 = *(_T4 + 16)
    _T6 = _T7
    _T9 = *(_T6 + 12)
    _T10 = (_T9 == _T4)
    _T8 = _T10
    if (_T8 == 0) branch _L19
    _T12 = *(_T6 + 12)
    _T11 = _T12
    branch _L20
_L19:
    _T13 = *(_T6 + 20)
    _T11 = _T13
_L20:
    _T14 = *(_T11 + 4)
    if (_T14 == 0) branch _L21
    _T15 = 0
    *(_T4 + 4) = _T15
    _T16 = 0
    *(_T11 + 4) = _T16
    _T17 = 1
    *(_T6 + 4) = _T17
    _T1 = _T6
    branch _L22
_L21:
    _T18 = *(_T4 + 12)
    _T19 = (_T18 == _T1)
    _T20 = (_T19 != _T8)
    if (_T20 == 0) branch _L23
    _T21 = *(_T0 + 0)
    _T22 = *(_T21 + 32)
    parm _T0
    parm _T1
    call _T22
    _T23 = _T1
    _T1 = _T4
    _T4 = _T23
    _T24 = *(_T4 + 16)
    _T6 = _T24
_L23:
    _T25 = 0
    *(_T4 + 4) = _T25
    _T26 = 1
    *(_T6 + 4) = _T26
    _T27 = *(_T0 + 0)
    _T28 = *(_T27 + 32)
    parm _T0
    parm _T4
    call _T28
_L22:
    branch _L18
_L17:
    _T29 = *(_T0 + 28)
    _T30 = 0
    *(_T29 + 4) = _T30
    return

FUNCTION<RBTree.insert>:
    _T3 = *(_T0 + 28)
    _T2 = _T3
    _T5 = *(_T0 + 24)
    _T4 = _T5
_L25:
    _T6 = *(_T0 + 24)
    _T7 = (_T2 != _T6)
    if (_T7 == 0) branch _L24
    _T4 = _T2
    _T8 = *(_T2 + 8)
    _T9 = (_T8 == _T1)
    if (_T9 == 0) branch _L26
    return
    branch _L27
_L26:
    _T10 = *(_T2 + 8)
    _T11 = (_T10 < _T1)
    if (_T11 == 0) branch _L28
    _T12 = *(_T2 + 20)
    _T2 = _T12
    branch _L29
_L28:
    _T13 = *(_T2 + 12)
    _T2 = _T13
_L29:
_L27:
    branch _L25
_L24:
    _T15 = *(_T0 + 24)
    parm _T4
    parm _T15
    parm _T1
    _T16 = call FUNCTION<Node.make>
    _T14 = _T16
    _T17 = *(_T0 + 24)
    _T18 = (_T4 == _T17)
    if (_T18 == 0) branch _L30
    *(_T0 + 28) = _T14
    branch _L31
_L30:
    _T19 = *(_T4 + 8)
    _T20 = (_T19 < _T1)
    if (_T20 == 0) branch _L32
    *(_T4 + 20) = _T14
    branch _L33
_L32:
    *(_T4 + 12) = _T14
_L33:
_L31:
    _T21 = *(_T0 + 0)
    _T22 = *(_T21 + 20)
    parm _T0
    parm _T14
    call _T22
    return

FUNCTION<RBTree.delete_fix>:
_L35:
    _T2 = *(_T0 + 28)
    _T3 = (_T1 != _T2)
    _T4 = *(_T1 + 4)
    _T5 = ! _T4
    _T6 = (_T3 && _T5)
    if (_T6 == 0) branch _L34
    _T8 = *(_T1 + 16)
    _T7 = _T8
    _T10 = *(_T7 + 12)
    _T11 = (_T10 == _T1)
    _T9 = _T11
    if (_T9 == 0) branch _L36
    _T13 = *(_T7 + 20)
    _T12 = _T13
    branch _L37
_L36:
    _T14 = *(_T7 + 12)
    _T12 = _T14
_L37:
    _T15 = *(_T12 + 4)
    if (_T15 == 0) branch _L38
    _T16 = 0
    *(_T12 + 4) = _T16
    _T17 = 1
    *(_T7 + 4) = _T17
    _T18 = *(_T0 + 0)
    _T19 = *(_T18 + 32)
    parm _T0
    parm _T12
    call _T19
    if (_T9 == 0) branch _L39
    _T20 = *(_T7 + 20)
    _T12 = _T20
    branch _L40
_L39:
    _T21 = *(_T7 + 12)
    _T12 = _T21
_L40:
_L38:
    _T22 = *(_T12 + 12)
    _T23 = *(_T22 + 4)
    _T24 = ! _T23
    _T25 = *(_T12 + 20)
    _T26 = *(_T25 + 4)
    _T27 = ! _T26
    _T28 = (_T24 && _T27)
    if (_T28 == 0) branch _L41
    _T29 = 0
    *(_T12 + 4) = _T29
    _T1 = _T7
    branch _L42
_L41:
    _T31 = *(_T12 + 20)
    _T30 = _T31
    _T33 = *(_T12 + 12)
    _T32 = _T33
    if (_T9 == 0) branch _L43
    _T34 = _T30
    _T30 = _T32
    _T32 = _T34
_L43:
    _T35 = *(_T32 + 4)
    _T36 = ! _T35
    if (_T36 == 0) branch _L44
    _T37 = 0
    *(_T30 + 4) = _T37
    _T38 = 1
    *(_T12 + 4) = _T38
    _T39 = *(_T0 + 0)
    _T40 = *(_T39 + 32)
    parm _T0
    parm _T30
    call _T40
    if (_T9 == 0) branch _L45
    _T41 = *(_T7 + 20)
    _T12 = _T41
    _T42 = *(_T12 + 20)
    _T32 = _T42
    branch _L46
_L45:
    _T43 = *(_T7 + 12)
    _T12 = _T43
    _T44 = *(_T12 + 12)
    _T32 = _T44
_L46:
_L44:
    _T45 = *(_T7 + 4)
    *(_T12 + 4) = _T45
    _T46 = 0
    *(_T7 + 4) = _T46
    _T47 = 0
    *(_T32 + 4) = _T47
    _T48 = *(_T0 + 0)
    _T49 = *(_T48 + 32)
    parm _T0
    parm _T12
    call _T49
    _T50 = *(_T0 + 28)
    _T1 = _T50
_L42:
    branch _L35
_L34:
    _T51 = 0
    *(_T1 + 4) = _T51
    return

FUNCTION<RBTree.delete>:
    _T3 = *(_T0 + 28)
    _T2 = _T3
_L48:
    _T4 = *(_T0 + 24)
    _T5 = (_T2 != _T4)
    if (_T5 == 0) branch _L47
    _T6 = *(_T2 + 8)
    _T7 = (_T6 == _T1)
    if (_T7 == 0) branch _L49
    branch _L47
    branch _L50
_L49:
    _T8 = *(_T2 + 8)
    _T9 = (_T8 < _T1)
    if (_T9 == 0) branch _L51
    _T10 = *(_T2 + 20)
    _T2 = _T10
    branch _L52
_L51:
    _T11 = *(_T2 + 12)
    _T2 = _T11
_L52:
_L50:
    branch _L48
_L47:
    _T12 = _T2
    _T15 = *(_T12 + 4)
    _T14 = _T15
    _T16 = *(_T2 + 12)
    _T17 = *(_T0 + 24)
    _T18 = (_T16 == _T17)
    if (_T18 == 0) branch _L53
    _T19 = *(_T2 + 20)
    _T13 = _T19
    _T20 = *(_T0 + 0)
    _T21 = *(_T20 + 36)
    parm _T0
    parm _T2
    parm _T13
    call _T21
    branch _L54
_L53:
    _T22 = *(_T2 + 20)
    _T23 = *(_T0 + 24)
    _T24 = (_T22 == _T23)
    if (_T24 == 0) branch _L55
    _T25 = *(_T2 + 12)
    _T13 = _T25
    _T26 = *(_T0 + 0)
    _T27 = *(_T26 + 36)
    parm _T0
    parm _T2
    parm _T13
    call _T27
    branch _L56
_L55:
    _T28 = *(_T2 + 20)
    _T12 = _T28
_L58:
    _T29 = *(_T12 + 12)
    _T30 = *(_T0 + 24)
    _T31 = (_T29 != _T30)
    if (_T31 == 0) branch _L57
    _T32 = *(_T12 + 12)
    _T12 = _T32
    branch _L58
_L57:
    _T33 = *(_T12 + 4)
    _T14 = _T33
    _T34 = *(_T12 + 20)
    _T13 = _T34
    _T35 = *(_T12 + 16)
    _T36 = (_T35 == _T2)
    if (_T36 == 0) branch _L59
    *(_T13 + 16) = _T12
    branch _L60
_L59:
    _T37 = *(_T0 + 0)
    _T38 = *(_T37 + 36)
    parm _T0
    parm _T12
    parm _T13
    call _T38
    _T39 = *(_T2 + 20)
    *(_T12 + 20) = _T39
    _T40 = *(_T12 + 20)
    *(_T40 + 16) = _T12
_L60:
    _T41 = *(_T0 + 0)
    _T42 = *(_T41 + 36)
    parm _T0
    parm _T2
    parm _T12
    call _T42
    _T43 = *(_T2 + 12)
    *(_T12 + 12) = _T43
    _T44 = *(_T12 + 12)
    *(_T44 + 16) = _T12
    _T45 = *(_T2 + 4)
    *(_T12 + 4) = _T45
_L56:
_L54:
    _T46 = ! _T14
    if (_T46 == 0) branch _L61
    _T47 = *(_T0 + 0)
    _T48 = *(_T47 + 12)
    parm _T0
    parm _T13
    call _T48
_L61:
    return

FUNCTION<RBTree.print>:
    _T1 = *(_T0 + 28)
    _T2 = *(_T0 + 0)
    _T3 = *(_T2 + 28)
    parm _T0
    parm _T1
    call _T3
    return

FUNCTION<RBTree.print_impl>:
    _T2 = *(_T0 + 24)
    _T3 = (_T1 == _T2)
    if (_T3 == 0) branch _L62
    return
    branch _L63
_L62:
    _T4 = *(_T1 + 12)
    _T5 = *(_T0 + 0)
    _T6 = *(_T5 + 28)
    parm _T0
    parm _T4
    call _T6
    _T7 = *(_T1 + 8)
    parm _T7
    call _PrintInt
    _T8 = " "
    parm _T8
    call _PrintString
    _T9 = *(_T1 + 20)
    _T10 = *(_T0 + 0)
    _T11 = *(_T10 + 28)
    parm _T0
    parm _T9
    call _T11
_L63:
    return

