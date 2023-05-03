# Python unit testing

# Import
import p1a #import bar
import p2a.p2b
import (
p3a,
p3b,
p3c,
)
"""Long Comment Test """

import subprocess

# Basic Alias
import p4a as p4alias
from p5a import * # import *
# from: Absolute External
from p6a import C1a
from p7a import C2a, C2b

from p8a import C3a as c3alias
from p9a import
from p10a import (C4a, C4b as c4balias, C4c)
from p11a import (
	C5a,
	C5b,
	C5c
)

def func():
	subprocess.run(" java -jar run.jar ")

# from: Absolute Internal
import ifoo
import ifoo.ibar as ifb
from ifoo.ibar import ifoobarModule
from ifoo.ibar.ifoobarModule import ifoobarClass
from ifoo.ibar import (
	ib,
	ia as IA,
	ir
)

# from: Relative
from . import ifoo
from . import (b, ifoo)
from . import (
	a,
	r
)

from .ifoo import ifee

from .. import Absolute
from ..Absolute import (b,
						a,
						r)

