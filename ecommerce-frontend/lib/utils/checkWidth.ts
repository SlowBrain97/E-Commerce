"use client"
import { useEffect, useState } from "react";

export default function checkWidth() {
    const [isDesktop, setIsDesktop] = useState(false);

    useEffect(() => {
        if (typeof window !== "undefined"){
            const check = () => setIsDesktop(window.innerWidth >= 768);
            check();
            window.addEventListener("resize", check);
            return () => window.removeEventListener("resize", check);
        }
    }, []);
    return isDesktop;
}
