# Here you can create play commands that are specific to the module
import os, os.path
import sys
import getopt
import subprocess

# Example below:
# ~~~~
if play_command == 'scaffold:gen':
	try:
		print "~ Generating controller and views from entities"
		print "~ "
		check_application()
		load_modules()
		do_classpath()
		try:
			# This is the new style to get the extra arg
			do_java('play.modules.scaffold.Generate', sys.argv)
		except Exception:
			# For play! < 1.0.3
			do_java('play.modules.scaffold.Generate')
		subprocess.call(java_cmd, env=os.environ)
		sys.exit(0)
				
	except getopt.GetoptError, err:
		print "~ Failed to generate scaffold properly..."
		print "~ %s" % str(err)
		print "~ "
		sys.exit(-1)
		
	sys.exit(0)