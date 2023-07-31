use p1a::p1b::p1c qw(SEGV BUS);

use strict; #should not be included
use warning; #should not be included
use integer; #should not be included
use bytes; #should not be included
use constant; #should not be included

use p2a ();
use p3a 12.34;
use if $var < 5.008, "p4a";

# Perl Versions (should not be included)
use v5.24.1;
use 1.23.4;
no v6.51.98.0;
no 6;
require v5.24.1;
require 5.24.1;

no integer; #should not be included
no strict 'refs'; #should not be included
no strict; #should not be included
no warning; #should not be included
no integer; #should not be included
no bytes; #should not be included
no constant; #should not be included

BEGIN { require p5a; p5a->import( c1a ); }
BEGIN { require p6a}
BEGIN { require p7a; p7a->VERSION(v12.34) }
require p8a::p8b;
use package::alias 'p9ABCD' => 'P9a::P9b::P9c::P9d';
use aliased 'P10a::P10b::P10c::P10d' => 'p10ABCD';
use namespace::alias 'P11a::P11b::P11c::P11d' => 'p11ABCD';
my $class = bar::foo;
    require $class1a;
require "lib/fee.pm";
use lib::bar; // Internal

#block comments

# Multiline comment
=a #as long as its "=[any letter]"
use pBAD1a ();
require pBAD2a;
require pBAD3a::pBAD3b;
use pBAD4a;
=cut;

use p12a;

# Multiline comment 2
=begin
use pBAD5a;
require pBAD6a;

use pBAD7a ();
=cut;

# Single line comment
# use pBAD8a;

use p13a;