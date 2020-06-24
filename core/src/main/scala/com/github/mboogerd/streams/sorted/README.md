# Design

Joins can be implemented as follows:

Known steps:

1. ContiguousGroupBy on both input streams (Key, Source[A/B])
2. Align the two streams (cast it to NEL[A] Ior NEL[B])

Steps to consider:
- Flatten the sub-streams in the appropriate manner (List[A/B] => TYPE]
    - Option (0-1)
    - List (0-N)
    - One (1-1)
    - Some (1-N)
- Recombine the substreams (A Ior B => OUT)
    - Tuple (should always have a Both alignment as input)
    - Ior (full-join with one side possibly empty)
