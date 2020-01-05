VTABLE<Complex>:
    NULL
    "Complex"
    FUNCTION<Complex.abs2>

VTABLE<Main>:
    VTABLE<Complex>
    "Main"
    FUNCTION<Complex.abs2>

FUNCTION<Main.new>:
    _T0 = 12
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Main>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Complex.new>:
    _T0 = 12
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Complex>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Complex.make>:
    _T3 = call FUNCTION<Complex.new>
    _T2 = _T3
    _T4 = 32768
    _T5 = (_T0 % _T4)
    *(_T2 + 8) = _T5
    _T6 = 32768
    _T7 = (_T1 % _T6)
    *(_T2 + 4) = _T7
    return _T2

FUNCTION<Complex.add>:
    _T2 = *(_T0 + 8)
    _T3 = *(_T1 + 8)
    _T4 = (_T2 + _T3)
    _T5 = *(_T0 + 4)
    _T6 = *(_T1 + 4)
    _T7 = (_T5 + _T6)
    parm _T4
    parm _T7
    _T8 = call FUNCTION<Complex.make>
    return _T8

FUNCTION<Complex.sub>:
    _T2 = *(_T0 + 8)
    _T3 = *(_T1 + 8)
    _T4 = (_T2 - _T3)
    _T5 = *(_T0 + 4)
    _T6 = *(_T1 + 4)
    _T7 = (_T5 - _T6)
    parm _T4
    parm _T7
    _T8 = call FUNCTION<Complex.make>
    return _T8

FUNCTION<Complex.mul>:
    _T2 = *(_T0 + 8)
    _T3 = *(_T1 + 8)
    _T4 = (_T2 * _T3)
    _T5 = *(_T0 + 4)
    _T6 = *(_T1 + 4)
    _T7 = (_T5 * _T6)
    _T8 = (_T4 - _T7)
    _T9 = 4096
    _T10 = (_T8 / _T9)
    _T11 = *(_T0 + 8)
    _T12 = *(_T1 + 4)
    _T13 = (_T11 * _T12)
    _T14 = *(_T0 + 4)
    _T15 = *(_T1 + 8)
    _T16 = (_T14 * _T15)
    _T17 = (_T13 + _T16)
    _T18 = 4096
    _T19 = (_T17 / _T18)
    parm _T10
    parm _T19
    _T20 = call FUNCTION<Complex.make>
    return _T20

FUNCTION<Complex.abs2>:
    _T1 = *(_T0 + 8)
    _T2 = *(_T0 + 8)
    _T3 = (_T1 * _T2)
    _T4 = *(_T0 + 4)
    _T5 = *(_T0 + 4)
    _T6 = (_T4 * _T5)
    _T7 = (_T3 + _T6)
    return _T7

main:
    _T1 = 51
    _T0 = _T1
    _T3 = 4096
    _T2 = _T3
    _T5 = 2
    _T6 = - _T5
    _T7 = (_T6 * _T2)
    _T4 = _T7
    _T9 = 4
    _T10 = (_T9 * _T2)
    _T11 = 1
    _T12 = (_T0 - _T11)
    _T13 = (_T10 / _T12)
    _T8 = _T13
    _T15 = 0
    _T16 = 1
    _T17 = (_T0 + _T16)
    _T18 = 4
    _T19 = (_T17 * _T18)
    parm _T19
    _T20 = call _Alloc
    *(_T20 + 0) = _T0
    _T21 = (_T20 + _T19)
    _T21 = (_T21 - _T18)
_L2:
    _T22 = (_T21 != _T20)
    if (_T22 == 0) branch _L1
    *(_T21 + 0) = _T15
    _T21 = (_T21 - _T18)
    branch _L2
_L1:
    _T23 = (_T20 + _T18)
    _T14 = _T23
    _T25 = 0
    _T26 = 1
    _T27 = (_T0 + _T26)
    _T28 = 4
    _T29 = (_T27 * _T28)
    parm _T29
    _T30 = call _Alloc
    *(_T30 + 0) = _T0
    _T31 = (_T30 + _T29)
    _T31 = (_T31 - _T28)
_L4:
    _T32 = (_T31 != _T30)
    if (_T32 == 0) branch _L3
    *(_T31 + 0) = _T25
    _T31 = (_T31 - _T28)
    branch _L4
_L3:
    _T33 = (_T30 + _T28)
    _T24 = _T33
    _T35 = 0
    _T34 = _T35
_L6:
    _T36 = (_T34 < _T0)
    if (_T36 == 0) branch _L5
    _T37 = 4
    _T38 = (_T34 * _T37)
    _T39 = (_T14 + _T38)
    _T40 = 0
    _T41 = 1
    _T42 = (_T0 + _T41)
    _T43 = 4
    _T44 = (_T42 * _T43)
    parm _T44
    _T45 = call _Alloc
    *(_T45 + 0) = _T0
    _T46 = (_T45 + _T44)
    _T46 = (_T46 - _T43)
_L8:
    _T47 = (_T46 != _T45)
    if (_T47 == 0) branch _L7
    *(_T46 + 0) = _T40
    _T46 = (_T46 - _T43)
    branch _L8
_L7:
    _T48 = (_T45 + _T43)
    *(_T39 + 0) = _T48
    _T49 = 4
    _T50 = (_T34 * _T49)
    _T51 = (_T24 + _T50)
    _T52 = 0
    _T53 = 1
    _T54 = (_T0 + _T53)
    _T55 = 4
    _T56 = (_T54 * _T55)
    parm _T56
    _T57 = call _Alloc
    *(_T57 + 0) = _T0
    _T58 = (_T57 + _T56)
    _T58 = (_T58 - _T55)
_L10:
    _T59 = (_T58 != _T57)
    if (_T59 == 0) branch _L9
    *(_T58 + 0) = _T52
    _T58 = (_T58 - _T55)
    branch _L10
_L9:
    _T60 = (_T57 + _T55)
    *(_T51 + 0) = _T60
    _T62 = 0
    _T61 = _T62
_L12:
    _T63 = (_T61 < _T0)
    if (_T63 == 0) branch _L11
    _T64 = 4
    _T65 = (_T34 * _T64)
    _T66 = (_T14 + _T65)
    _T67 = *(_T66 + 0)
    _T68 = 4
    _T69 = (_T61 * _T68)
    _T70 = (_T67 + _T69)
    _T71 = (_T61 * _T8)
    _T72 = (_T4 + _T71)
    _T73 = (_T34 * _T8)
    _T74 = (_T4 + _T73)
    parm _T72
    parm _T74
    _T75 = call FUNCTION<Complex.make>
    *(_T70 + 0) = _T75
    _T76 = 4
    _T77 = (_T34 * _T76)
    _T78 = (_T24 + _T77)
    _T79 = *(_T78 + 0)
    _T80 = 4
    _T81 = (_T61 * _T80)
    _T82 = (_T79 + _T81)
    _T83 = call FUNCTION<Complex.new>
    *(_T82 + 0) = _T83
    _T84 = 1
    _T85 = (_T61 + _T84)
    _T61 = _T85
    branch _L12
_L11:
    _T86 = 1
    _T87 = (_T34 + _T86)
    _T34 = _T87
    branch _L6
_L5:
    _T89 = 0
    _T88 = _T89
_L14:
    _T90 = 20
    _T91 = (_T88 < _T90)
    if (_T91 == 0) branch _L13
    _T93 = 0
    _T92 = _T93
_L16:
    _T94 = (_T92 < _T0)
    if (_T94 == 0) branch _L15
    _T96 = 0
    _T95 = _T96
_L18:
    _T97 = (_T95 < _T0)
    if (_T97 == 0) branch _L17
    _T99 = 4
    _T100 = (_T92 * _T99)
    _T101 = (_T24 + _T100)
    _T102 = *(_T101 + 0)
    _T103 = 4
    _T104 = (_T95 * _T103)
    _T105 = (_T102 + _T104)
    _T106 = *(_T105 + 0)
    _T98 = _T106
    _T107 = *(_T98 + 0)
    _T108 = *(_T107 + 8)
    parm _T98
    _T109 = call _T108
    _T110 = 4
    _T111 = (_T110 * _T2)
    _T112 = (_T111 * _T2)
    _T113 = (_T109 < _T112)
    if (_T113 == 0) branch _L19
    _T114 = 4
    _T115 = (_T92 * _T114)
    _T116 = (_T24 + _T115)
    _T117 = *(_T116 + 0)
    _T118 = 4
    _T119 = (_T95 * _T118)
    _T120 = (_T117 + _T119)
    parm _T98
    parm _T98
    _T121 = call FUNCTION<Complex.mul>
    _T122 = 4
    _T123 = (_T92 * _T122)
    _T124 = (_T14 + _T123)
    _T125 = *(_T124 + 0)
    _T126 = 4
    _T127 = (_T95 * _T126)
    _T128 = (_T125 + _T127)
    _T129 = *(_T128 + 0)
    parm _T121
    parm _T129
    _T130 = call FUNCTION<Complex.add>
    *(_T120 + 0) = _T130
_L19:
    _T131 = 1
    _T132 = (_T95 + _T131)
    _T95 = _T132
    branch _L18
_L17:
    _T133 = 1
    _T134 = (_T92 + _T133)
    _T92 = _T134
    branch _L16
_L15:
    _T135 = 1
    _T136 = (_T88 + _T135)
    _T88 = _T136
    branch _L14
_L13:
    _T138 = 0
    _T137 = _T138
_L21:
    _T139 = (_T137 < _T0)
    if (_T139 == 0) branch _L20
    _T141 = 0
    _T140 = _T141
_L23:
    _T142 = (_T140 < _T0)
    if (_T142 == 0) branch _L22
    _T143 = 4
    _T144 = (_T137 * _T143)
    _T145 = (_T24 + _T144)
    _T146 = *(_T145 + 0)
    _T147 = 4
    _T148 = (_T140 * _T147)
    _T149 = (_T146 + _T148)
    _T150 = *(_T149 + 0)
    _T151 = *(_T150 + 0)
    _T152 = *(_T151 + 8)
    parm _T150
    _T153 = call _T152
    _T154 = 4
    _T155 = (_T154 * _T2)
    _T156 = (_T155 * _T2)
    _T157 = (_T153 < _T156)
    if (_T157 == 0) branch _L24
    _T158 = "**"
    parm _T158
    call _PrintString
    branch _L25
_L24:
    _T159 = "  "
    parm _T159
    call _PrintString
_L25:
    _T160 = 1
    _T161 = (_T140 + _T160)
    _T140 = _T161
    branch _L23
_L22:
    _T162 = "\n"
    parm _T162
    call _PrintString
    _T163 = 1
    _T164 = (_T137 + _T163)
    _T137 = _T164
    branch _L21
_L20:
    return

