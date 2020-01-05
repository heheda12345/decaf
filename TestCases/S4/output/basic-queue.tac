VTABLE<Main>:
    NULL
    "Main"

VTABLE<Queue>:
    NULL
    "Queue"
    FUNCTION<Queue.DeQueue>
    FUNCTION<Queue.EnQueue>
    FUNCTION<Queue.Init>

VTABLE<QueueItem>:
    NULL
    "QueueItem"
    FUNCTION<QueueItem.GetData>
    FUNCTION<QueueItem.GetNext>
    FUNCTION<QueueItem.GetPrev>
    FUNCTION<QueueItem.Init>
    FUNCTION<QueueItem.SetNext>
    FUNCTION<QueueItem.SetPrev>

FUNCTION<QueueItem.new>:
    _T0 = 16
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<QueueItem>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Queue.new>:
    _T0 = 12
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Queue>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<Main.new>:
    _T0 = 4
    parm _T0
    _T1 = call _Alloc
    _T2 = VTABLE<Main>
    *(_T1 + 0) = _T2
    return _T1

FUNCTION<QueueItem.Init>:
    *(_T0 + 4) = _T1
    *(_T0 + 8) = _T2
    *(_T2 + 12) = _T0
    *(_T0 + 12) = _T3
    *(_T3 + 8) = _T0
    return

FUNCTION<QueueItem.GetData>:
    _T1 = *(_T0 + 4)
    return _T1

FUNCTION<QueueItem.GetNext>:
    _T1 = *(_T0 + 8)
    return _T1

FUNCTION<QueueItem.GetPrev>:
    _T1 = *(_T0 + 12)
    return _T1

FUNCTION<QueueItem.SetNext>:
    *(_T0 + 8) = _T1
    return

FUNCTION<QueueItem.SetPrev>:
    *(_T0 + 12) = _T1
    return

FUNCTION<Queue.Init>:
    _T1 = call FUNCTION<QueueItem.new>
    *(_T0 + 4) = _T1
    _T2 = 0
    _T3 = *(_T0 + 4)
    _T4 = *(_T0 + 4)
    _T5 = *(_T0 + 4)
    _T6 = *(_T5 + 0)
    _T7 = *(_T6 + 20)
    parm _T5
    parm _T2
    parm _T3
    parm _T4
    call _T7
    return

FUNCTION<Queue.EnQueue>:
    _T3 = call FUNCTION<QueueItem.new>
    _T2 = _T3
    _T4 = *(_T0 + 4)
    _T5 = *(_T4 + 0)
    _T6 = *(_T5 + 12)
    parm _T4
    _T7 = call _T6
    _T8 = *(_T0 + 4)
    _T9 = *(_T2 + 0)
    _T10 = *(_T9 + 20)
    parm _T2
    parm _T1
    parm _T7
    parm _T8
    call _T10
    return

FUNCTION<Queue.DeQueue>:
    _T2 = *(_T0 + 4)
    _T3 = *(_T2 + 0)
    _T4 = *(_T3 + 16)
    parm _T2
    _T5 = call _T4
    _T6 = *(_T0 + 4)
    _T7 = (_T5 == _T6)
    if (_T7 == 0) branch _L1
    _T8 = "Queue Is Empty"
    parm _T8
    call _PrintString
    _T9 = 0
    return _T9
    branch _L2
_L1:
    _T11 = *(_T0 + 4)
    _T12 = *(_T11 + 0)
    _T13 = *(_T12 + 16)
    parm _T11
    _T14 = call _T13
    _T10 = _T14
    _T15 = *(_T10 + 0)
    _T16 = *(_T15 + 8)
    parm _T10
    _T17 = call _T16
    _T1 = _T17
    _T18 = *(_T10 + 0)
    _T19 = *(_T18 + 12)
    parm _T10
    _T20 = call _T19
    _T21 = *(_T10 + 0)
    _T22 = *(_T21 + 16)
    parm _T10
    _T23 = call _T22
    _T24 = *(_T23 + 0)
    _T25 = *(_T24 + 24)
    parm _T23
    parm _T20
    call _T25
    _T26 = *(_T10 + 0)
    _T27 = *(_T26 + 16)
    parm _T10
    _T28 = call _T27
    _T29 = *(_T10 + 0)
    _T30 = *(_T29 + 12)
    parm _T10
    _T31 = call _T30
    _T32 = *(_T31 + 0)
    _T33 = *(_T32 + 28)
    parm _T31
    parm _T28
    call _T33
_L2:
    return _T1

main:
    _T2 = call FUNCTION<Queue.new>
    _T0 = _T2
    _T3 = *(_T0 + 0)
    _T4 = *(_T3 + 16)
    parm _T0
    call _T4
    _T5 = 0
    _T1 = _T5
_L4:
    _T6 = 10
    _T7 = (_T1 < _T6)
    if (_T7 == 0) branch _L3
    _T8 = *(_T0 + 0)
    _T9 = *(_T8 + 12)
    parm _T0
    parm _T1
    call _T9
    _T10 = 1
    _T11 = (_T1 + _T10)
    _T1 = _T11
    branch _L4
_L3:
    _T12 = 0
    _T1 = _T12
_L6:
    _T13 = 4
    _T14 = (_T1 < _T13)
    if (_T14 == 0) branch _L5
    _T15 = *(_T0 + 0)
    _T16 = *(_T15 + 8)
    parm _T0
    _T17 = call _T16
    parm _T17
    call _PrintInt
    _T18 = " "
    parm _T18
    call _PrintString
    _T19 = 1
    _T20 = (_T1 + _T19)
    _T1 = _T20
    branch _L6
_L5:
    _T21 = "\n"
    parm _T21
    call _PrintString
    _T22 = 0
    _T1 = _T22
_L8:
    _T23 = 10
    _T24 = (_T1 < _T23)
    if (_T24 == 0) branch _L7
    _T25 = *(_T0 + 0)
    _T26 = *(_T25 + 12)
    parm _T0
    parm _T1
    call _T26
    _T27 = 1
    _T28 = (_T1 + _T27)
    _T1 = _T28
    branch _L8
_L7:
    _T29 = 0
    _T1 = _T29
_L10:
    _T30 = 17
    _T31 = (_T1 < _T30)
    if (_T31 == 0) branch _L9
    _T32 = *(_T0 + 0)
    _T33 = *(_T32 + 8)
    parm _T0
    _T34 = call _T33
    parm _T34
    call _PrintInt
    _T35 = " "
    parm _T35
    call _PrintString
    _T36 = 1
    _T37 = (_T1 + _T36)
    _T1 = _T37
    branch _L10
_L9:
    _T38 = "\n"
    parm _T38
    call _PrintString
    return

