import unittest
from unittest.mock import mock_open, patch
from upm.upm_core.detector import get_distro

class TestDistroDetection(unittest.TestCase):

    @patch("builtins.open", new_callable=mock_open, read_data="""NAME="Ubuntu"
VERSION="20.04.3 LTS (Focal Fossa)"
ID=ubuntu
ID_LIKE=debian
PRETTY_NAME="Ubuntu 20.04.3 LTS" """)
    def test_ubuntu_detection(self, mock_file):
        self.assertEqual(get_distro(), "ubuntu")

    @patch("builtins.open", new_callable=mock_open, read_data="""NAME="Debian GNU/Linux"
VERSION_ID="11"
ID=debian
PRETTY_NAME="Debian GNU/Linux 11 (bullseye)" """)
    def test_debian_detection(self, mock_file):
        self.assertEqual(get_distro(), "debian")

    @patch("builtins.open", new_callable=mock_open, read_data="""NAME="Fedora Linux"
VERSION_ID="35"
ID=fedora
PRETTY_NAME="Fedora Linux 35" """)
    def test_fedora_detection(self, mock_file):
        self.assertEqual(get_distro(), "fedora")

    @patch("builtins.open", new_callable=mock_open, read_data="""NAME="CentOS Linux"
VERSION_ID="8"
ID=centos
PRETTY_NAME="CentOS Linux 8" """)
    def test_other_distro_detection(self, mock_file):
        self.assertEqual(get_distro(), "unknown")

    @patch("builtins.open", new_callable=mock_open, read_data="""ID="sles"
NAME="SUSE Linux Enterprise Server" """)
    def test_sles_distro_detection(self, mock_file):
        self.assertEqual(get_distro(), "unknown")

    @patch("builtins.open", side_effect=FileNotFoundError)
    def test_file_not_found(self, mock_file):
        self.assertEqual(get_distro(), "unknown")

    @patch("builtins.open", new_callable=mock_open, read_data='INVALID_FORMAT')
    def test_invalid_format_no_id(self, mock_file):
        self.assertEqual(get_distro(), "unknown")

    @patch("builtins.open", new_callable=mock_open, read_data='ID=""') # Empty ID
    def test_empty_id(self, mock_file):
        self.assertEqual(get_distro(), "unknown")

if __name__ == "__main__":
    unittest.main()
