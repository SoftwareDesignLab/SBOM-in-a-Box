### Require Imports
require "p1a"
require 'p2a_p3a'

### Internal Imports
require 'lib/bar' # require internal
require './bar' # require internal 2
load './bar.rb' # load internal

### Language Imports
require 'cgi/session' # require language
load 'cgi/session/memorystore' # load language

### Load Imports
load "p4a"
autoload p5a

### Script Init (not counted)
Require_no_authentication #should not count
yaml.load(file) #should not count
script = File.join(File.dirname(__FILE__), ARGV.shift) #should not count
script += '.rb' unless FileTest.exist?(script) #should not count
puts "Script: #{script}" #should not count


### Comments
# load "pBAD1a" #should not count
# require "pBAD2a" #should not count

### Block Comments

=begin # In a block comment should not count
load pBAD3a #should not count
require pBAD4a #should not count
=end #should not count

# Between block comments should count
load p6a
require "p7a"

=begin
load pBAD5a #should not count
require pBAD6a #should not count
=end