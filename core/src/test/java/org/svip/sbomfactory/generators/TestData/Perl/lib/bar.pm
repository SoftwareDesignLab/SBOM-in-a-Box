package bar;

use strict;
use warnings;
use Exporter 'import';
our $VERSION = '1.00';
our @EXPORT  = qw(say_hello);

sub say_hello {print "hello!"}

1;