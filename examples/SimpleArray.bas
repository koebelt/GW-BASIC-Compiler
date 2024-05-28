10 DIM NUMS(5)
20 FOR I = 1 TO 5
30   INPUT "Enter a number: ", NUMS(I)
40 NEXT I
50 PRINT "Numbers in reverse order:"
60 FOR I = 5 TO 1 STEP -1
70   PRINT NUMS(I)
80 NEXT I
90 END
