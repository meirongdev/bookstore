import { Link, NavLink } from "react-router-dom";
import { navLinks } from "../../../constants/constants";
import hamburgerMenuLogo from "../../../assets/icons/hamburger-menu.svg";
import xMenuLogo from "../../../assets/icons/x-menu.svg";
import { useState } from "react";
import { useAuthenticationContext } from "../../../authentication/authenticationContext";

export const Navbar = () => {

    const { authentication, logout } = useAuthenticationContext();

    const [hamburgerMenuClicked, setHamburgerMenuClicked] = useState(false);

    return (

        <header className="bg-gray-900 border-b border-gray-700 z-10 w-full h-[60px] fixed top-0 left-0 right-0">

            <div className="container-centered h-full">
                <nav className="flex justify-between items-center h-full">

                    <NavLink to="/" className="font-semibold text-lg text-white">
                        BookStore
                    </NavLink>

                <div className={`${!hamburgerMenuClicked && "max-lg:hidden"} nav-menu`}>

                    {navLinks.map((link) => {
                        let shouldRender = false;
                        if (link.authRequired) {
                            if (authentication.isAuthenticated) {
                                if (link.adminOnly) {
                                    shouldRender = authentication.authority === "ROLE_ADMIN";
                                } else {
                                    shouldRender = true;
                                }
                            }
                        } else {
                            shouldRender = true;
                        }

                        if (!shouldRender) return null;

                        return (
                            <NavLink
                                key={link.id}
                                className={({ isActive }) => (isActive ? "nav-link-active" : "nav-link")}
                                to={link.href}
                                onClick={() => setHamburgerMenuClicked(false)}
                            >
                                {link.title}
                            </NavLink>
                        );
                    })}

                    {authentication.isAuthenticated ?
                        <button className="btn-secondary lg:hidden mt-2" onClick={logout}>Log out</button>
                        :
                        <Link to={"/login"} className="btn-secondary lg:hidden mt-2" onClick={() => setHamburgerMenuClicked(false)}>Sign In</Link>
                    }

                </div>

                {authentication.isAuthenticated ?
                    <button className="btn-secondary max-lg:hidden" onClick={logout}>Log out</button>
                    :
                    <Link to={"/login"} className="btn-secondary max-lg:hidden">Sign In</Link>
                }

                    <button className="p-1 hidden max-lg:block" onClick={() => setHamburgerMenuClicked(!hamburgerMenuClicked)}>
                        <img src={hamburgerMenuClicked ? xMenuLogo : hamburgerMenuLogo} alt="menu" width={28} height={28} />
                    </button>

                </nav>
            </div>

        </header>

    )
}
