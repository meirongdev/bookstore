import gitLogo from "../../../assets/icons/github-logo-teal.svg";
import linkedInLogo from "../../../assets/icons/linkedin-logo-teal.svg";

export const Footer = () => {
    return (
        <footer className="bg-gray-800 text-white py-6">
            <div className="container mx-auto px-4">
                <div className="flex flex-col md:flex-row justify-between items-center gap-4">

                    {/* Brand */}
                    <div className="text-center md:text-left">
                        <p className="font-bold text-lg">BookStore</p>
                        <p className="text-sm text-gray-400">Developed by MR</p>
                    </div>

                    {/* Social Links */}
                    <div className="flex items-center gap-4">
                        <a
                            href={import.meta.env.VITE_GITHUB_LINK}
                            target="_blank"
                            rel="noopener noreferrer"
                            aria-label="GitHub"
                            className="p-2 rounded-full bg-gray-700 hover:bg-teal-500 transition-colors duration-300"
                        >
                            <img className="w-5 h-5" src={gitLogo} alt="GitHub" />
                        </a>
                        <a
                            href={import.meta.env.VITE_LINKEDIN_LINK}
                            target="_blank"
                            rel="noopener noreferrer"
                            aria-label="LinkedIn"
                            className="p-2 rounded-full bg-gray-700 hover:bg-teal-500 transition-colors duration-300"
                        >
                            <img className="w-5 h-5" src={linkedInLogo} alt="LinkedIn" />
                        </a>
                    </div>

                    {/* Contact */}
                    <div className="text-center md:text-right">
                        <p className="text-sm text-gray-400">meirongdev@gmail.com</p>
                    </div>

                </div>

                {/* Copyright */}
                <div className="mt-4 pt-4 border-t border-gray-700 text-center">
                    <p className="text-xs text-gray-500">© {new Date().getFullYear()} BookStore. All rights reserved.</p>
                </div>
            </div>
        </footer>
    );
}
