package main

import "p1a"
import "p2a/p2b/c1a"
import "github.com/gopherguides/greet" // this imports 3rd party

// Language/3rd party
import "fmt" // Import fmt
import "math/big" // Import big from math
import . "regexp" // Import all from regexp

// Internal
import "Go/lib/int2" // imports int2.go

// Multi-line import
import (
	"p3a"
	"p3b"
)
// With multipart
import (
	"p4a"
	"p4b/p4c/p4d/c2a"
)
// With alias
import (
	p5aliasA "p5a"
	p5aliasB "p5b"
)
// Inline, semicolon separated
import ("p6a"; "p6b")
// With alias
import (p7aliasA "p7a"; p7aliasB "p7b")

// Alias cases
import p8alias "p8a"
import p9alias "p9a/p9b"
import . "p10a" // Treat as "*" (imports all methods)
import _ "pBAD1a" // Make sure to exclude (unused if "_" as alias)
import "p11a"

import pBAD2a // Invalid import

// Single-line Comment
// import "pBAD3a/pBAD3b"

// Multi-line Comments
/* import "pBAD4a" */
/* import "pBAD5a/pBAD5b" */
/* import "pBAD6a/pBAD6b"
import ("pBAD7a" "pBAD7b")

import "pBAD8a/pBAD8b"
*/

// Between Multi-line Comments
/**/ import "p12a/p12b" /**/


