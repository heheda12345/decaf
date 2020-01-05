VTABLE<Main>:
    NULL
    "Main"

VTABLE<MergeSort>:
    NULL
    "MergeSort"

VTABLE<QuickSort>:
    NULL
    "QuickSort"

VTABLE<Rng>:
    NULL
    "Rng"
    FUNCTION<Rng.next>

FUNCTION<QuickSort.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<QuickSort>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Rng.new>:
    _T0 = 8
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Rng>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<MergeSort.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<MergeSort>
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
    _T4 = 500
    _T5 = 0
    _T6 = 1
    _T7 = (_T4 + _T6)
    _T8 = 4
    _T9 = (_T7 * _T8)
    parm _T9
    _T10 = call _Alloc
    *(_T10 + 0) = _T4
    _T11 = (_T10 + _T9)
    _T11 = (_T11 - _T8)
_L2:
    _T12 = (_T11 != _T10)
    if (_T12 == 0) branch _L1
    *(_T11 + 0) = _T5
    _T11 = (_T11 - _T8)
    branch _L2
_L1:
    _T13 = (_T10 + _T8)
    _T3 = _T13
    _T15 = 500
    _T16 = 0
    _T17 = 1
    _T18 = (_T15 + _T17)
    _T19 = 4
    _T20 = (_T18 * _T19)
    parm _T20
    _T21 = call _Alloc
    *(_T21 + 0) = _T15
    _T22 = (_T21 + _T20)
    _T22 = (_T22 - _T19)
_L4:
    _T23 = (_T22 != _T21)
    if (_T23 == 0) branch _L3
    *(_T22 + 0) = _T16
    _T22 = (_T22 - _T19)
    branch _L4
_L3:
    _T24 = (_T21 + _T19)
    _T14 = _T24
    _T26 = 0
    _T25 = _T26
_L6:
    _T27 = *(_T3 - 4)
    _T28 = (_T25 < _T27)
    if (_T28 == 0) branch _L5
    _T29 = 4
    _T30 = (_T25 * _T29)
    _T31 = (_T3 + _T30)
    _T32 = *(_T0 + 0)
    _T33 = *(_T32 + 8)
    parm _T0
    _T34 = call _T33
    _T35 = 500
    _T36 = (_T34 % _T35)
    *(_T31 + 0) = _T36
    _T37 = 4
    _T38 = (_T25 * _T37)
    _T39 = (_T14 + _T38)
    _T40 = 4
    _T41 = (_T25 * _T40)
    _T42 = (_T3 + _T41)
    _T43 = *(_T42 + 0)
    *(_T39 + 0) = _T43
    _T44 = 1
    _T45 = (_T25 + _T44)
    _T25 = _T45
    branch _L6
_L5:
    _T46 = 0
    _T47 = *(_T3 - 4)
    _T48 = 1
    _T49 = (_T47 - _T48)
    parm _T3
    parm _T46
    parm _T49
    call FUNCTION<QuickSort.sort>
    _T51 = 0
    _T50 = _T51
_L8:
    _T52 = *(_T3 - 4)
    _T53 = (_T50 < _T52)
    if (_T53 == 0) branch _L7
    _T54 = 4
    _T55 = (_T50 * _T54)
    _T56 = (_T3 + _T55)
    _T57 = *(_T56 + 0)
    parm _T57
    call _PrintInt
    _T58 = " "
    parm _T58
    call _PrintString
    _T59 = 1
    _T60 = (_T50 + _T59)
    _T50 = _T60
    branch _L8
_L7:
    _T61 = "\n"
    parm _T61
    call _PrintString
    parm _T14
    call FUNCTION<MergeSort.sort>
    _T63 = 0
    _T62 = _T63
_L10:
    _T64 = *(_T14 - 4)
    _T65 = (_T62 < _T64)
    if (_T65 == 0) branch _L9
    _T66 = 4
    _T67 = (_T62 * _T66)
    _T68 = (_T14 + _T67)
    _T69 = *(_T68 + 0)
    parm _T69
    call _PrintInt
    _T70 = " "
    parm _T70
    call _PrintString
    _T71 = 1
    _T72 = (_T62 + _T71)
    _T62 = _T72
    branch _L10
_L9:
    _T73 = "\n"
    parm _T73
    call _PrintString
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

FUNCTION<QuickSort.sort>:
    _T3 = _T1
    _T4 = _T2
    _T6 = (_T2 - _T1)
    _T7 = 2
    _T8 = (_T6 / _T7)
    _T9 = (_T1 + _T8)
    _T10 = 4
    _T11 = (_T9 * _T10)
    _T12 = (_T0 + _T11)
    _T13 = *(_T12 + 0)
    _T5 = _T13
_L12:
    _T14 = (_T3 <= _T4)
    if (_T14 == 0) branch _L11
_L14:
    _T15 = 4
    _T16 = (_T3 * _T15)
    _T17 = (_T0 + _T16)
    _T18 = *(_T17 + 0)
    _T19 = (_T18 < _T5)
    if (_T19 == 0) branch _L13
    _T20 = 1
    _T21 = (_T3 + _T20)
    _T3 = _T21
    branch _L14
_L13:
_L16:
    _T22 = 4
    _T23 = (_T4 * _T22)
    _T24 = (_T0 + _T23)
    _T25 = *(_T24 + 0)
    _T26 = (_T25 > _T5)
    if (_T26 == 0) branch _L15
    _T27 = 1
    _T28 = (_T4 - _T27)
    _T4 = _T28
    branch _L16
_L15:
    _T29 = (_T3 <= _T4)
    if (_T29 == 0) branch _L17
    _T31 = 4
    _T32 = (_T3 * _T31)
    _T33 = (_T0 + _T32)
    _T34 = *(_T33 + 0)
    _T30 = _T34
    _T35 = 4
    _T36 = (_T3 * _T35)
    _T37 = (_T0 + _T36)
    _T38 = 4
    _T39 = (_T4 * _T38)
    _T40 = (_T0 + _T39)
    _T41 = *(_T40 + 0)
    *(_T37 + 0) = _T41
    _T42 = 4
    _T43 = (_T4 * _T42)
    _T44 = (_T0 + _T43)
    *(_T44 + 0) = _T30
    _T45 = 1
    _T46 = (_T3 + _T45)
    _T3 = _T46
    _T47 = 1
    _T48 = (_T4 - _T47)
    _T4 = _T48
_L17:
    branch _L12
_L11:
    _T49 = (_T1 < _T4)
    if (_T49 == 0) branch _L18
    parm _T0
    parm _T1
    parm _T4
    call FUNCTION<QuickSort.sort>
_L18:
    _T50 = (_T3 < _T2)
    if (_T50 == 0) branch _L19
    parm _T0
    parm _T3
    parm _T2
    call FUNCTION<QuickSort.sort>
_L19:
    return

FUNCTION<MergeSort.sort>:
    _T1 = 0
    _T2 = *(_T0 - 4)
    _T3 = *(_T0 - 4)
    _T4 = 0
    _T5 = 1
    _T6 = (_T3 + _T5)
    _T7 = 4
    _T8 = (_T6 * _T7)
    parm _T8
    _T9 = call _Alloc
    *(_T9 + 0) = _T3
    _T10 = (_T9 + _T8)
    _T10 = (_T10 - _T7)
_L21:
    _T11 = (_T10 != _T9)
    if (_T11 == 0) branch _L20
    *(_T10 + 0) = _T4
    _T10 = (_T10 - _T7)
    branch _L21
_L20:
    _T12 = (_T9 + _T7)
    parm _T0
    parm _T1
    parm _T2
    parm _T12
    call FUNCTION<MergeSort.sort_impl>
    return

FUNCTION<MergeSort.sort_impl>:
    _T4 = 1
    _T5 = (_T1 + _T4)
    _T6 = (_T5 < _T2)
    if (_T6 == 0) branch _L22
    _T8 = (_T1 + _T2)
    _T9 = 2
    _T10 = (_T8 / _T9)
    _T7 = _T10
    parm _T0
    parm _T1
    parm _T7
    parm _T3
    call FUNCTION<MergeSort.sort_impl>
    parm _T0
    parm _T7
    parm _T2
    parm _T3
    call FUNCTION<MergeSort.sort_impl>
    _T11 = _T1
    _T12 = _T7
    _T14 = 0
    _T13 = _T14
_L24:
    _T15 = (_T11 < _T7)
    _T16 = (_T12 < _T2)
    _T17 = (_T15 && _T16)
    if (_T17 == 0) branch _L23
    _T18 = 4
    _T19 = (_T12 * _T18)
    _T20 = (_T0 + _T19)
    _T21 = *(_T20 + 0)
    _T22 = 4
    _T23 = (_T11 * _T22)
    _T24 = (_T0 + _T23)
    _T25 = *(_T24 + 0)
    _T26 = (_T21 < _T25)
    if (_T26 == 0) branch _L25
    _T27 = 4
    _T28 = (_T13 * _T27)
    _T29 = (_T3 + _T28)
    _T30 = 4
    _T31 = (_T12 * _T30)
    _T32 = (_T0 + _T31)
    _T33 = *(_T32 + 0)
    *(_T29 + 0) = _T33
    _T34 = 1
    _T35 = (_T12 + _T34)
    _T12 = _T35
    branch _L26
_L25:
    _T36 = 4
    _T37 = (_T13 * _T36)
    _T38 = (_T3 + _T37)
    _T39 = 4
    _T40 = (_T11 * _T39)
    _T41 = (_T0 + _T40)
    _T42 = *(_T41 + 0)
    *(_T38 + 0) = _T42
    _T43 = 1
    _T44 = (_T11 + _T43)
    _T11 = _T44
_L26:
    _T45 = 1
    _T46 = (_T13 + _T45)
    _T13 = _T46
    branch _L24
_L23:
_L28:
    _T47 = (_T11 < _T7)
    if (_T47 == 0) branch _L27
    _T48 = 4
    _T49 = (_T13 * _T48)
    _T50 = (_T3 + _T49)
    _T51 = 4
    _T52 = (_T11 * _T51)
    _T53 = (_T0 + _T52)
    _T54 = *(_T53 + 0)
    *(_T50 + 0) = _T54
    _T55 = 1
    _T56 = (_T13 + _T55)
    _T13 = _T56
    _T57 = 1
    _T58 = (_T11 + _T57)
    _T11 = _T58
    branch _L28
_L27:
    _T59 = 0
    _T11 = _T59
_L30:
    _T60 = (_T11 < _T13)
    if (_T60 == 0) branch _L29
    _T61 = (_T11 + _T1)
    _T62 = 4
    _T63 = (_T61 * _T62)
    _T64 = (_T0 + _T63)
    _T65 = 4
    _T66 = (_T11 * _T65)
    _T67 = (_T3 + _T66)
    _T68 = *(_T67 + 0)
    *(_T64 + 0) = _T68
    _T69 = 1
    _T70 = (_T11 + _T69)
    _T11 = _T70
    branch _L30
_L29:
_L22:
    return

