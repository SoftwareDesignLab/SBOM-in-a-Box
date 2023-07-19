// External statments

use p1a::p1b::c1a;
use ::p2a::p2b;
use P3a::p3b;
use p4a::{
p4b::p4c::C2a as c2alias,
p4b::p4c::{c3a as c3alias, c3b, c3c, c3d},
p4d::p4e::C4a, p4f::c5a,
p4b::p4c::{c6a, c6b,
c6c}, p4g::c7a, p4g::p4h::{c8a, c8b,
c8c}
};


// Internal statements
extern lib far;
use lib::bar;
mod tar;

// Language statements

// Imports one of each possible import type
use std::fmt::{Result, Debug, Arguments, Alignment, Display, format};
// Imports "self" which refers to all (*)
use std::path::{self, Path, PathBuf};

// Module imports
mod c13a;
mod crate::bar;

// Self imports
use self::p5a::c9a as x;
use self::p6a::c10a as _;
Pub use self::p7a::{c11a, c11b};
mod self; // Invalid import

// Misc. imports
use p9a_p9b::C12a;
use p8a::*; // import "*"
extern p10a c13a; // extern import


// Comments

// Single-line comments
// use pBAD1a::pBAD1b::cBAD1a;
//! use pBAD2a::pBAD2b::cBAD2a;

// Multi-line comments
/* use pBAD3a::pBAD3b::cBAD3a; */
/*! use pBAD4a::pBAD4b::cBAD4a;*/
/*
use pBAD5a::pBAD5b::cBAD5a;
*/
/*!
use pBAD6a::pBAD6b::cBAD6a;
*/
/** use pBAD7a::pBAD7b::cBAD7a; */
/**
use pBAD8a::pBAD8b::cBAD8a;
*/

// Between multiline comments
/**/ use p11a::p11b::c14a; /**/
/**/ mod p12a::p12b::c15a; /**/
/**/ extern p13a p13b c16a; /**/