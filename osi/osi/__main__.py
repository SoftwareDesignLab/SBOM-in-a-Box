"""
file: __main__.py
description: main entrypoint to start the osi server

@author Derek Garcia
"""
from argparse import ArgumentParser, Namespace

from osi_server import OSIAPIServer


def _create_parser() -> ArgumentParser:
    parser = ArgumentParser(
        description="Launch osi server",
        prog="osi"
    )

    # Create subparsers for different commands
    commands = parser.add_subparsers(dest='command', required=True)

    # server command
    server = commands.add_parser('server', help="Launch API server")
    server.add_argument('-H', '--host',
                        metavar="<domain>",
                        type=str,
                        help=f"Host of API server (Default: {OSIAPIServer.DEFAULT_FLASK_HOST}",
                        default=OSIAPIServer.DEFAULT_FLASK_HOST)

    server.add_argument('-p', '--port',
                        metavar="<port>",
                        type=int,
                        help=f"Port of API server (Default: {OSIAPIServer.DEFAULT_FLASK_PORT})",
                        default=OSIAPIServer.DEFAULT_FLASK_PORT)

    return parser


def _execute(args: Namespace) -> None:
    """
    Execute command

    :param args: args to get command details from
    """
    # Launch server
    if args.command == 'server':
        OSIAPIServer(args.host, args.port).run()
        return


def main() -> None:
    """
    Parse initial arguments and execute commands
    """
    args = _create_parser().parse_args()
    _execute(args)


if __name__ == "__main__":
    main()
