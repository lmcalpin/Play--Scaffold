# Here you can create play commands that are specific to the module
import os, os.path
import sys
import getopt
import subprocess

try:
    from play.utils import package_as_war
    PLAY10 = False
except ImportError:
    PLAY10 = True

MODULE = 'scaffold'

COMMANDS = ['scaffold:gen']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    print "executing command: " + command
    if command == 'scaffold:gen':
        run(app, args)

def run(app, args):
    app.check()
    java_cmd = app.java_cmd(['-Xmx64m'], className='play.modules.scaffold.Generate', args=[args])
    subprocess.call(java_cmd, env=os.environ)
    print

if PLAY10:
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